package com.uni.cookoff.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UpdateContestRequest {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer roundNumber;
    private Boolean isActive;
    private Integer maxParticipants;
}
