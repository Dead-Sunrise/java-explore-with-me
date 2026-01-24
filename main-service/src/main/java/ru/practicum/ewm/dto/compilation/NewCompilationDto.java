package ru.practicum.ewm.dto.compilation;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {

    @Builder.Default
    private Boolean pinned = false;

    @NotBlank
    private String title;

    private List<Long> events;
}
