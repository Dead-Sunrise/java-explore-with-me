package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.stats.server.mapper.HitMapper;
import ru.practicum.ewm.stats.server.model.Hit;
import ru.practicum.ewm.stats.server.repository.HitRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final HitRepository hitRepository;

    @Override
    public HitDto createHit(HitDto hitDto) {
        Hit hit = HitMapper.hitDtoToHit(hitDto);
        return HitMapper.hitToHitDto(hitRepository.save(hit));
    }

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return hitRepository.getUniqueStats(start, end);
            } else {
                return hitRepository.getStats(start, end);
            }
        } else {
            if (unique) {
                return hitRepository.getUniqueStatsWithUris(start, end, uris);
            } else {
                return hitRepository.getStatsWithUris(start, end, uris);
            }
        }
    }
}
