package com.newtonbox.validators;

import com.newtonbox.models.Comment;
import com.newtonbox.models.Experiment;
import com.newtonbox.repository.ICommentRepository;
import com.newtonbox.repository.IExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("commentValidator")
public class CommentValidator {

    @Autowired
    private ICommentRepository commentRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    /**
     * Verifica si el usuario autenticado es el creador del comentario
     */
    public boolean isCommentOwner (Long commentId, Long authenticatedUserId){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        return comment.getUser().getId().equals(authenticatedUserId);
    }


    /**
     * Verifica si el usuario autenticado es participante del experimento al que pertenece el comentario
     */
    public boolean isParticipantInExperiment (Long commentId, Long authenticatedUserId){
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        return comment.getExperiment().getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(authenticatedUserId));
    }

    /**
     * Verifica si el usuario es el creador del experimento al que pertenece el comentario
     */
    public boolean isCommentExperimentOwner (Long commentId, Long authenticatedUserId) {
        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        return comment.getExperiment().getCreatedBy().getId().equals(authenticatedUserId);
    }

    /**
     * Verifica si el usuario ha comentado en un experimento
     */
    public boolean hasCommentInExperiment (Long experimentId, Long authenticatedUserId){
        // Obtiene la lista de comentario del experimento
        List<Comment> comments = commentRepo.findByExperiment(Experiment.builder().id(experimentId).build());
        return comments.stream().anyMatch(c -> c.getUser().getId().equals(authenticatedUserId));
    }

    /**
     * Verifica si el usuario es participante de un experimento concreto
     */
    public boolean isParticipantOfExperiment(Long experimentId, Long userId) {
        Experiment exp = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        // Retorna true si el usuario es uno de los participantes
        return exp.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
    }

    /**
     * Verifica si el usuario es el creador de un experimento específico
     */
    public boolean isExperimentCreator(Long experimentId, Long userId) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));

        return experiment.getCreatedBy().getId().equals(userId);
    }
}
