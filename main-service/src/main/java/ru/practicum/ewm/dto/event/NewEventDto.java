package ru.practicum.ewm.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.location.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {

    @NotBlank
    private String annotation;

    @NotNull
    private Long category;

    private String description;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    @Positive
    private Integer participantLimit;

    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank
    private String title;
}
