package com.newtonbox.mapper;

import com.newtonbox.models.Comment;
import com.newtonbox.models.Experiment;
import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.CommentDTO;


public class CommentMapper {

    public static CommentDTO toDTO (Comment comment){
        if(comment == null) return null;

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .username(comment.getUser().getUsername())
                .experimentId(comment.getExperiment().getId())
                .timestamp(comment.getTimestamp())
                .build();
    }

    public static Comment toEntity(CommentDTO commentDTO){
        if(commentDTO == null) return null;

        Comment comment = new Comment();
        comment.setId(commentDTO.getId());
        comment.setContent(commentDTO.getContent());

        if (commentDTO.getUsername() != null){
            UserEntity user = new UserEntity();
            user.setUsername(commentDTO.getUsername());
            comment.setUser(user);
        }

        if (commentDTO.getExperimentId() != null){
            Experiment experiment = new Experiment();
            experiment.setId(commentDTO.getExperimentId());
            comment.setExperiment(experiment);
        }
        return comment;
    }
}
