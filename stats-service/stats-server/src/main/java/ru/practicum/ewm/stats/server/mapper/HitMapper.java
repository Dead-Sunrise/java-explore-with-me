package ru.practicum.ewm.stats.server.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.stats.server.model.Hit;

@Component
@RequiredArgsConstructor
public class HitMapper {
    public static Hit hitDtoToHit(HitDto hitDto) {
        return Hit.builder()
                .id(hitDto.getId())
                .app(hitDto.getApp())
                .uri(hitDto.getUri())
                .ip(hitDto.getIp())
                .created(hitDto.getCreated())
                .build();
    }

    public static HitDto hitToHitDto(Hit hit) {
        return HitDto.builder()
                .id(hit.getId())
                .app(hit.getApp())
                .uri(hit.getUri())
                .ip(hit.getIp())
                .created(hit.getCreated())
                .build();
    }
}
