package com.uni.cookoff.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CreateContestRequest {
    private String name;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int roundNumber;
    private boolean isActive;
    private Integer maxParticipants;
}
