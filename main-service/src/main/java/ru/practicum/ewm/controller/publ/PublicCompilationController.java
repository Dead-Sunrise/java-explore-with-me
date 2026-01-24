package ru.practicum.ewm.controller.publ;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(required = false, defaultValue = "0") Integer from,
                                                   @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("GET запрос на получение списка подборок.");
        return compilationService.publicGetCompilations(pinned, from, size);
    }

    @GetMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(@PathVariable Long compilationId) {
        log.info("GET запрос на получение подборки по id.");
        return compilationService.publicGetCompilationById(compilationId);
    }
}
