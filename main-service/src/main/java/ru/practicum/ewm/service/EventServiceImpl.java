package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.enums.StateAction;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.mapper.LocationMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.repository.RequestRepository;
import ru.practicum.ewm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventFullDto privateCreateEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата начала события должна быть не ранее чем через 2 часа от текущего времени.");
        }
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь по id: " + userId + " не существует."));
        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория по id: " + newEventDto.getCategory() + " не существует."));
        Event event = eventMapper.newEventDtoToEvent(newEventDto);
        event.setCategory(category);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto privateUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено, либо пользователь не является инициатором."));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Событие со статусом PUBLISHED опубликовано, обновление невозможно.");
        }
        if (updateEventUserRequest.getStateAction() != null) {
            if (updateEventUserRequest.getStateAction() == StateAction.SEND_TO_REVIEW) {
                event.setState(EventState.PENDING);
            }
            if (updateEventUserRequest.getStateAction() == StateAction.CANCEL_REVIEW) {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEventUserRequest.getEventDate() != null
                && updateEventUserRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата начала события должна быть не ранее чем через 2 часа от текущего времени.");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория по id: " + updateEventUserRequest.getCategory() + " не существует."));
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(LocationMapper.locationDtoToLocation(updateEventUserRequest.getLocation()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional
    public EventFullDto privateGetUserEventById(Long userId, Long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не существует."));
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Событие не найдено, либо пользователь не является инициатором."));
        List<EventFullDto> dtoList = List.of(eventMapper.eventToEventFullDto(event));
        dtoList = addViewsAndRequestsInFullDto(dtoList);
        return dtoList.getFirst();
    }

    @Override
    @Transactional
    public List<EventShortDto> privateGetUserEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не существует."));
        Pageable pageable = PageRequest.of(from / size, size);
        List<EventShortDto> dtoList = eventRepository.findAllByInitiatorId(userId, pageable).stream()
                .map(EventMapper::eventToEventShortDto)
                .collect(Collectors.toList());
        return addViewsAndRequestsInShortsDto(dtoList);
    }

    @Override
    @Transactional
    public EventFullDto publicGetEventById(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не существует."));
        if (event.getState() != EventState.PUBLISHED) {
            throw new NotFoundException("Событие с id: " + eventId + " не опубликовано, просмотр не доступен.");
        }
        List<EventFullDto> dtoList = List.of(eventMapper.eventToEventFullDto(event));
        dtoList = addViewsAndRequestsInFullDto(dtoList);
        return dtoList.getFirst();
    }

    @Override
    @Transactional
    public List<EventShortDto> publicSearchEvents(String ip, String text, List<Long> categories, Boolean paid,
                                                  LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                                  Boolean onlyAvailable, String sort, Integer from, Integer size) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 10;
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть положительным числом.");
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала события должна быть раньше даты окончания.");
        }
        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? rangeEnd : LocalDateTime.now().plusYears(100);
        String searchText = (text != null) ? text : "";
        Pageable pageable;
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());
        } else {
            pageable = PageRequest.of(from / size, size);
        }
        List<EventFullDto> events = eventRepository.publicSearchEvents(EventState.PUBLISHED, searchText, categories, paid,
                        start, end, onlyAvailable != null && onlyAvailable, pageable).stream()
                .map(eventMapper::eventToEventFullDto)
                .toList();
        events = addViewsAndRequestsInFullDto(events);
        if ("VIEWS".equalsIgnoreCase(sort)) {
            events.sort(Comparator.comparing(EventFullDto::getViews));
        }
        int sortStart = Math.min(from, events.size());
        int sortEnd = Math.min(sortStart + size, events.size());
        events = events.subList(sortStart, sortEnd);
        sendViews(events, ip);
        return events.stream()
                .map(eventMapper::eventFullDtoToEventShortDto)
                .toList();
    }

    @Override
    @Transactional
    public EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id: " + eventId + " не существует."));
        if (updateEventAdminRequest.getStateAction() == StateAction.PUBLISH_EVENT) {
            if (event.getState() != EventState.PENDING) {
                throw new ConflictException("Событие с id: " + eventId + " в неподходящем статусе для публикации.");
            } else {
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            }
        } else if (updateEventAdminRequest.getStateAction() == StateAction.REJECT_EVENT) {
            if (event.getState() == EventState.PUBLISHED) {
                throw new ConflictException("Отклонить публикацию опубликованного события невозможно.");
            } else {
                event.setState(EventState.CANCELED);
            }
        }
        if (updateEventAdminRequest.getEventDate() != null
                && updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("Дата начала события должна быть не ранее чем через 2 часа от текущего времени.");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new NotFoundException("Категория по id: " + updateEventAdminRequest.getCategory() + " не существует."));
            event.setCategory(category);
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(LocationMapper.locationDtoToLocation(updateEventAdminRequest.getLocation()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> adminSearchEvents(String ip, List<Long> users, List<EventState> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        if (from == null) {
            from = 0;
        }
        if (size == null) {
            size = 10;
        }
        if (size <= 0) {
            throw new BadRequestException("Параметр size должен быть положительным числом.");
        }
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Дата начала события должна быть раньше даты окончания.");
        }
        LocalDateTime start = (rangeStart != null) ? rangeStart : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? rangeEnd : LocalDateTime.now().plusYears(100);
        Pageable pageable = PageRequest.of(from / size, size);
        if (users != null && users.isEmpty()) {
            users = null;
        }
        if (states != null && states.isEmpty()) {
            states = null;
        }
        if (categories != null && categories.isEmpty()) {
            categories = null;
        }
        List<EventFullDto> events = eventRepository.adminSearchEvents(users, states, categories, start, end, pageable)
                .stream()
                .map(eventMapper::eventToEventFullDto)
                .toList();
        events = addViewsAndRequestsInFullDto(events);
        return events;
    }

    private List<EventShortDto> addViewsAndRequestsInShortsDto(List<EventShortDto> dtoList) {
        List<Long> ids = dtoList
                .stream()
                .map(EventShortDto::getId)
                .collect(Collectors.toList());
        HashMap<Long, Long> idAndViews = statsClient.getStatsById(ids, "/events/").getIdAndViews();
        dtoList.forEach(dto -> dto.setViews(idAndViews.getOrDefault(dto.getId(), 0L)));
        dtoList.forEach(dto -> {
            Integer count = requestRepository.countByEventIdAndStatus(dto.getId(), RequestStatus.CONFIRMED).intValue();
            dto.setConfirmedRequests(count != null ? count : 0);
        });
        return dtoList;
    }

    private List<EventFullDto> addViewsAndRequestsInFullDto(List<EventFullDto> dtoList) {
        List<Long> ids = dtoList
                .stream()
                .map(EventFullDto::getId)
                .collect(Collectors.toList());
        HashMap<Long, Long> idAndViews = statsClient.getStatsById(ids, "/events/").getIdAndViews();
        dtoList.forEach(dto -> dto.setViews(idAndViews.getOrDefault(dto.getId(), 0L)));
        dtoList.forEach(dto -> {
            Integer count = requestRepository.countByEventIdAndStatus(dto.getId(), RequestStatus.CONFIRMED).intValue();
            dto.setConfirmedRequests(count != null ? count : 0);
        });
        return dtoList;
    }

    private void sendViews(List<EventFullDto> events, String ip) {
        events.forEach(e -> {
            statsClient.createHit(HitDto.builder()
                    .app("ewm-main-service")
                    .uri("/events/" + e.getId())
                    .ip(ip)
                    .timestamp(LocalDateTime.now())
                    .build()
            );
        });
    }
}