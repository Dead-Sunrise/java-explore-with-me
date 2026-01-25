package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.dto.StatsDto;
import ru.practicum.ewm.stats.server.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {
    @Query("""
            SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<StatsDto> getUniqueStats(@Param("start") LocalDateTime start,
                                  @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(h))
            FROM Hit h
            WHERE h.timestamp BETWEEN :start AND :end
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
            """)
    List<StatsDto> getStats(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);

    @Query("""
            SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(DISTINCT h.ip))
            FROM Hit h
            WHERE h.timestamp BETWEEN :start AND :end
                AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(DISTINCT h.ip) DESC
            """)
    List<StatsDto> getUniqueStatsWithUris(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end,
                                          @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.ewm.dto.StatsDto(h.app, h.uri, COUNT(h))
            FROM Hit h
            WHERE h.timestamp BETWEEN :start AND :end
                AND h.uri IN :uris
            GROUP BY h.app, h.uri
            ORDER BY COUNT(h) DESC
            """)
    List<StatsDto> getStatsWithUris(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);

    @Query("""
            SELECT new ru.practicum.ewm.dto.StatsDto(hit.app, hit.uri,
            COUNT(DISTINCT hit.ip))
            FROM Hit hit
            WHERE hit.uri IN :uris
            GROUP BY hit.app, hit.uri
            ORDER BY COUNT(DISTINCT hit.ip) DESC
            """)
    List<StatsDto> getUniqueStatsByUris(@Param("uris") List<String> uris);
}