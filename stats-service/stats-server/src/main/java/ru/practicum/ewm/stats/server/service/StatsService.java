package ru.practicum.ewm.stats.server.service;

import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.dto.StatsDtoById;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    HitDto createHit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    StatsDtoById getStatsById(List<Long> ids, String basicAddress);
}
