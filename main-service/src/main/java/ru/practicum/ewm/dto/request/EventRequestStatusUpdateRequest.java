package ru.practicum.ewm.dto.request;

import lombok.*;
import ru.practicum.ewm.enums.RequestStatus;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
