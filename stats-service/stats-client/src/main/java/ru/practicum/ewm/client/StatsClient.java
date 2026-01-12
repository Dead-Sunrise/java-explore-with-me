package ru.practicum.ewm.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.dto.HitDto;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class StatsClient {
    private final RestTemplate restTemplate;
    private final String statsUrl;

    public StatsClient(RestTemplate restTemplate, @Value("${stats-server.url}") String statsUrl) {
        this.restTemplate = restTemplate;
        this.statsUrl = statsUrl;
    }

    public void createHit(HitDto hitDto) {
        restTemplate.postForEntity(
                statsUrl + "/hit",
                hitDto,
                Void.class
        );
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start == null || end == null) {
            throw new ValidationException("Значения диапазона даты должны быть указаны.");
        }
        if (start.isAfter(end)) {
            throw new ValidationException("Дата окончания в диапазоне не должна быть раньше начала");
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
                .fromHttpUrl(statsUrl + "/stats")
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);
        if (uris != null && !uris.isEmpty()) {
            uris.forEach(uri -> uriBuilder.queryParam("uris", uri));
        }
        ResponseEntity<StatsDto[]> responseEntity = restTemplate.getForEntity(uriBuilder.toUriString(), StatsDto[].class);
        return Arrays.asList(responseEntity.getBody());
    }
}
