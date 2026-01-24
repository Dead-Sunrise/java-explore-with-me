package ru.practicum.ewm.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> ids);

    Boolean existsByCategoryId(Long id);

    @Query("SELECT e FROM Event e " +
            "WHERE e.state = :state " +
            "AND (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate BETWEEN :start AND :end " +
            "AND (:onlyAvailable = false OR e.participantLimit = 0 " +
            "OR e.participantLimit > (SELECT COUNT(r) FROM Request r " +
            "WHERE r.event = e AND r.status = 'CONFIRMED'))")
    List<Event> publicSearchEvents(@Param("state") EventState state,
                                   @Param("text") String text,
                                   @Param("categories") List<Long> categories,
                                   @Param("paid") Boolean paid,
                                   @Param("start") LocalDateTime start,
                                   @Param("end") LocalDateTime end,
                                   @Param("onlyAvailable") boolean onlyAvailable,
                                   Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id in :categories) " +
            "AND (e.eventDate >= :rangeStart) " +
            "AND (e.eventDate <= COALESCE(:rangeEnd, e.eventDate))")
    List<Event> adminSearchEvents(@Param("users") List<Long> users,
                                  @Param("states") List<EventState> states,
                                  @Param("categories") List<Long> categories,
                                  @Param("rangeStart") LocalDateTime rangeStart,
                                  @Param("rangeEnd") LocalDateTime rangeEnd,
                                  Pageable pageable);
}
