package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.dto.compilation.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto adminCreateCompilation(NewCompilationDto newCompilationDto);

    CompilationDto adminUpdateCompilation(Long compilationId, UpdateCompilationRequest updateCompilationRequest);

    void adminDeleteCompilation(Long compilationId);

    CompilationDto publicGetCompilationById(Long compilationId);

    List<CompilationDto> publicGetCompilations(Boolean pinned, Integer from, Integer size);
}