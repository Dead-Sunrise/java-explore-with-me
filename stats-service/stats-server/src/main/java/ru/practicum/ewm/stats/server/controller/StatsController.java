package ru.practicum.ewm.stats.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.dto.StatsDtoById;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RequestMapping
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto createHit(@RequestBody @Valid HitDto hitDto) {
        return statsService.createHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") boolean unique
    ) {
        if (start.isAfter(end)) {
            throw new ValidationException("Дата окончания в диапазоне не должна быть раньше начала.");
        }
        return statsService.getStats(start, end, uris, unique);
    }

    @GetMapping("/statsById")
    public StatsDtoById getStatsById(@RequestParam List<Long> ids, @RequestParam String basicAddress) {
        return statsService.getStatsById(ids, basicAddress);
    }
}
