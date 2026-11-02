package com.newtonbox.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentDTO {

    private Long id;
    private String title;
    private String description;
    private String variables;
    private String createdBy;
    private List<ParticipantDTO> participants;
    private List<ResultDTO> results;
    private List<CommentDTO> comments;
}
