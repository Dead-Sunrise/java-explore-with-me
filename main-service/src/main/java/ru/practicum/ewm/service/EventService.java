package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto privateCreateEvent(Long userId, NewEventDto newEventDto);

    EventFullDto privateUpdateEvent(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    EventFullDto privateGetUserEventById(Long userId, Long eventId);

    List<EventShortDto> privateGetUserEvents(Long userId, Integer from, Integer size);

    EventFullDto publicGetEventById(Long eventId);

    List<EventShortDto> publicSearchEvents(String ip, String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                           LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from, Integer size);

    EventFullDto adminUpdateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventFullDto> adminSearchEvents(String ip, List<Long> users, List<EventState> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);
}
