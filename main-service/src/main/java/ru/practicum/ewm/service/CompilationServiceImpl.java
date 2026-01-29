package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;


    @Override
    @Transactional
    public CompilationDto adminCreateCompilation(NewCompilationDto newCompilationDto) {
        List<Event> events = Collections.emptyList();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
            if (newCompilationDto.getEvents().size() != events.size()) {
                throw new NotFoundException("Часть событий из списка не найдена.");
            }
        }
        Compilation compilation = CompilationMapper.newCompilationDtoToCompilation(newCompilationDto, events);
        return CompilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public CompilationDto adminUpdateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка с id: " + compilationId + " не существует."));
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            compilation.setEvents(eventRepository.findAllByIdIn(updateCompilationRequest.getEvents()));
        }
        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        return CompilationMapper.compilationToCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void adminDeleteCompilation(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка с id: " + compilationId + " не существует."));
        compilationRepository.deleteById(compilationId);
    }

    @Override
    public CompilationDto publicGetCompilationById(Long compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException("Подборка с id: " + compilationId + " не существует."));
        return CompilationMapper.compilationToCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> publicGetCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        Pageable pageable = PageRequest.of(from / size, size);
        if (pinned == null) {
            compilations = compilationRepository.findAll(pageable).getContent();
        } else {
            compilations = compilationRepository.findByPinned(pinned, pageable);
        }
        return compilations.stream()
                .map(CompilationMapper::compilationToCompilationDto)
                .collect(Collectors.toList());
    }
}
