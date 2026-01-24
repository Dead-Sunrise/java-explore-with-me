package ru.practicum.ewm.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompilationRequest {
    private Boolean pinned;
    private String title;
    private List<Long> events;
}
