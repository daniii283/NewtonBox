package com.newtonbox.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipantDTO {

    private Long id;
    private String username;
    private String role;
    private Long experimentId;
}
