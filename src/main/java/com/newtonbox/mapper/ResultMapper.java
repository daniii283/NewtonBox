package com.newtonbox.mapper;

import com.newtonbox.models.Result;
import com.newtonbox.dto.ResultDTO;

public class ResultMapper {

    public static ResultDTO toDTO (Result result){
        if(result == null) return null;

        return ResultDTO.builder()
                .id(result.getId())
                .data(result.getData())
                .experimentId(result.getExperiment() != null ? result.getExperiment().getId() : null)
                .experimentTitle(result.getExperiment() != null ? result.getExperiment().getTitle() : null)
                .createdByUsername(result.getCreatedBy() != null ? result.getCreatedBy().getUsername() : null)
                .build();
    }

    public static Result toEntity(ResultDTO resultDTO){
        if(resultDTO == null) return null;

        Result result = new Result();
        result.setId(resultDTO.getId());
        result.setData(resultDTO.getData());

        return result;
    }
}
