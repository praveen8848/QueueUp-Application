package com.queueup.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class TicketResponse {
    private Long id;
    private String token;
    private String name;
    private int position;
    private LocalDateTime joinedAt;
    private int estimatedWaitMinutes;

    public TicketResponse() {}

    public TicketResponse(String token, String name, int position,
                          LocalDateTime joinedAt, int estimatedWaitMinutes) {
        this.token = token;
        this.name = name;
        this.position = position;
        this.joinedAt = joinedAt;
        this.estimatedWaitMinutes = estimatedWaitMinutes;
    }

}