package com.newtonbox.mapper;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.ExperimentDTO;
import java.util.stream.Collectors;

public class ExperimentMapper{

    public static ExperimentDTO toDTO(Experiment experiment) {
        if (experiment == null) return null;

        return ExperimentDTO.builder()
                .id(experiment.getId())
                .title(experiment.getTitle())
                .description(experiment.getDescription())
                .variables(experiment.getVariables())
                
                .createdBy(experiment.getCreatedBy().getUsername())
                .participants(
                        experiment.getParticipants().stream()
                                .map(ParticipantMapper::toDTO) // Esto devuelve ParticipantDTO
                                .collect(Collectors.toList())
                )
                .results(
                        experiment.getResults().stream()
                                .map(ResultMapper::toDTO)
                                .collect(Collectors.toList())
                )
                .comments(
                        experiment.getComments().stream()
                                .map(CommentMapper::toDTO)
                                .collect(Collectors.toList())
                )
                .build();
    }


    public static Experiment toEntity(ExperimentDTO experimentDTO){

        Experiment experiment = new Experiment();

        experiment.setId(experimentDTO.getId());
        experiment.setTitle(experimentDTO.getTitle());
        experiment.setDescription(experimentDTO.getDescription());
        experiment.setVariables(experimentDTO.getVariables());

        if (experiment.getCreatedBy() != null){
           UserEntity user = new UserEntity();
           user.setUsername(experimentDTO.getCreatedBy());
           experiment.setCreatedBy(user);
        }

        return experiment;

    }
}
