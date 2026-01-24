package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.enums.RequestStatus;
import ru.practicum.ewm.model.Request;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countByEvent_IdAndStatus(Long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(Long requesterId);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByEventId(Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);
}
