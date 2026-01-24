package ru.practicum.ewm.controller.admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.UpdateEventAdminRequest;
import ru.practicum.ewm.enums.EventState;
import ru.practicum.ewm.service.EventService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
@Slf4j
public class AdminEventController {
    private final EventService eventService;
    private final StatsClient statsClient;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> searchEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {
        log.info("GET запрос на поиск события администратором по параметрам.");
        statsClient.createHit(request, "main-service");
        return eventService.adminSearchEvents(request.getRemoteAddr(), users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(@PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("PATCH запрос на обновление события администратором.");
        return eventService.adminUpdateEvent(eventId, updateEventAdminRequest);
    }
}
