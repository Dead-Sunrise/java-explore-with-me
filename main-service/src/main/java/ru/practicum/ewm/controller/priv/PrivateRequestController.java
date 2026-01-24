package ru.practicum.ewm.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.request.ParticipationRequestDto;
import ru.practicum.ewm.service.RequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
@Slf4j
@Validated
public class PrivateRequestController {
    private final RequestService requestService;

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        log.info("POST запрос на создание запроса на участие в событии.");
        return requestService.createRequest(userId, eventId);
    }

    @GetMapping("/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable Long userId) {
        log.info("GET запрос на получение запросов пользователя.");
        return requestService.getRequestsByUserId(userId);
    }

    @PatchMapping("requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable Long userId,
                                                 @PathVariable Long requestId) {
        log.info("PATCH запрос на отмену запроса пользователем.");
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByUserId(@PathVariable Long userId,
                                                                  @PathVariable Long eventId) {
        log.info("GET запрос на получение запросов в событии пользователя.");
        return requestService.getEventRequests(userId, eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info("GET запрос на обновление статуса запросов на участие в событии пользователя.");
        return requestService.updateRequestStatus(userId, eventId, updateRequest);
    }
}
