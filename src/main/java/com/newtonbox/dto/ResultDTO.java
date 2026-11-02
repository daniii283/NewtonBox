package com.newtonbox.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDTO {

    private Long id;
    private String data;
    private Long experimentId;
    private String experimentTitle;
    private String createdByUsername;

}
