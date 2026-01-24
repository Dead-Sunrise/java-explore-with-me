package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.dto.user.UserShortDto;
import ru.practicum.ewm.enums.EventState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventFullDto {

    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdOn;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}
