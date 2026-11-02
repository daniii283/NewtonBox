package com.newtonbox.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private String content;
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime timestamp;
    private Long experimentId;

}
