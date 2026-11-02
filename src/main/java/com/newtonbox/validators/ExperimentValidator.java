package com.newtonbox.validators;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.Participant;
import com.newtonbox.models.Permission;
import com.newtonbox.models.Role;
import com.newtonbox.repository.IExperimentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("experimentValidator")
public class ExperimentValidator {

    @Autowired
    private IExperimentRepository experimentRepo;

    /**
     * Verifica si el usuario (por username) es el creador del experimento
     */
    public boolean isCreator(Long experimentId, String username) {
        Experiment exp = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        return exp.getCreatedBy().getUsername().equals(username);
    }

    /**
     * Verifica si el usuario está involucrado en el experimento (como creador o participante)
     */
    public boolean isUserAllowed(Long experimentId, String username) {
        Experiment exp = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        boolean isCreator = exp.getCreatedBy().getUsername().equals(username);
        boolean isParticipant = exp.getParticipants().stream()
                .anyMatch(p -> p.getUser().getUsername().equals(username));
        return isCreator || isParticipant;
    }

    /**
     * Verifica si el usuario tiene permiso específico en el experimento
     */
    public boolean hasPermission(Long experimentId, String username, String permission) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));

        // Si el usuario es el creador, se le otorgan permisos completos
        if (experiment.getCreatedBy().getUsername().equalsIgnoreCase(username)) {
            return true;
        }

        // Recorre los participantes para encontrar el rol y sus permisos
        for (Participant participant : experiment.getParticipants()) {
            if (participant.getUser().getUsername().equalsIgnoreCase(username)) {
                for (Role role : participant.getUser().getRoles()) {
                    for (Permission perm : role.getPermissionsList()) {
                        if (perm.getPermissionEnum().name().equalsIgnoreCase(permission)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
