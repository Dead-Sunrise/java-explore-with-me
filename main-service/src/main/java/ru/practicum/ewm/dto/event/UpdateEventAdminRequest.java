package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.location.LocationDto;
import ru.practicum.ewm.enums.StateAction;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateEventAdminRequest {

    private String annotation;

    private Long category;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @Positive
    private Integer participantLimit;

    private Boolean requestModeration;

    private String title;

    private StateAction stateAction;
}
