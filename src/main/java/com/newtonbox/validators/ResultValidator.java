package com.newtonbox.validators;

import com.newtonbox.models.Experiment;
import com.newtonbox.repository.IExperimentRepository;
import com.newtonbox.repository.IParticipantRepository;
import com.newtonbox.repository.IResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResultValidator {

    @Autowired
    private IResultRepository resultRepo;

    @Autowired
    private IParticipantRepository participantRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    /**
     * Verifica si el usuario autenticaddo tiene permiso para acceder a un resultado concreto
     */
    public boolean canAccessResult(Long resultId, Long userId) {
        return resultRepo.findById(resultId).map(result -> {
            Experiment experiment = result.getExperiment();

            boolean isCreator = experiment.getCreatedBy().getId().equals(userId);
            boolean isParticipant = experiment.getParticipants().stream()
                    .anyMatch(p -> p.getUser().getId().equals(userId));

            return isCreator || isParticipant;

        }).orElse(false);
    }

    /**
     * Verifica si el usuario autenticado tiene permiso para crear un resultado en un experimento concreto
     */
    public boolean canCreateResultInExperiment(Long experimentId, Long userId) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));

        boolean isCreator = experiment.getCreatedBy().getId().equals(userId);

        boolean isParticipant = participantRepo.findByExperiment(experiment).stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));

        return isCreator || isParticipant;
    }

    /**
     * Verifica si el usuario tiene permiso para actualizar un resultado
     */
    public boolean canUpdateResult (Long resultId, Long userId){
        return resultRepo.findById(resultId).map(result -> {
            Experiment experiment = result.getExperiment();

            // Verifica si el usuario es el creador del resultado
            boolean isResultCreator = result.getCreatedBy() != null && result.getCreatedBy().getId().equals(userId);

            // Verifica si el usuario es el creador del experimento asociado
            boolean isExperiementCreator = experiment.getCreatedBy().getId().equals(userId);

            return isResultCreator || isExperiementCreator;
        }).orElse(false);
    }

    /**
     * Verifica si el usuario tiene permiso para eliminar un resultado
     * (debe ser el creador del resultado o el creador del experimento asociado)
     */
    public boolean canDeleteResult (Long resultId, Long userId){
        return resultRepo.findById(resultId).map(result -> {
            Experiment experiment = result.getExperiment();

            boolean isResultCreator = result.getCreatedBy() != null && result.getCreatedBy().getId().equals(userId);

            boolean isExperimentCreator = experiment.getCreatedBy() != null && experiment.getCreatedBy().getId().equals(userId);

            return isResultCreator || isExperimentCreator;
        }).orElse(false);
    }

    /**
     * Verifica si el usuario tiene permiso para ecceder a los resultados de un experimento
     */
    public boolean canAccessResultOfExperiment (Long experimentId, Long userId){
        return experimentRepo.findById(experimentId).map(experiment -> {
            boolean isCreator = experiment.getCreatedBy().getId().equals(userId);
            boolean isParticipant = experiment.getParticipants().stream()
                    .anyMatch(p -> p.getUser().getId().equals(userId));

            return isCreator || isParticipant;
        }).orElse(false);
    }
}
