package com.newtonbox.validators;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.Participant;
import com.newtonbox.repository.IExperimentRepository;
import com.newtonbox.repository.IParticipantRepository;
import com.newtonbox.repository.IUserRepository;
import com.newtonbox.dto.ParticipantDTO;
import com.newtonbox.utils.RoleEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component("participantValidator")
public class ParticipantValidator {

    @Autowired
    private IParticipantRepository participantRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    @Autowired
    private IUserRepository userRepo;

    /**
     * Verifica que el usuario autenticado (por ID) sea el creador del experimento
     * asociado al registro de participación
     */
    public boolean isCreatorOfParticipant (Long participantId, Long authenticatedUserId){
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));
        return participant.getExperiment().getCreatedBy().getId().equals(authenticatedUserId);
    }

    /**
     * Verifica que el usuario autenticado sea el dueño (propietario) del registro de participación
     */
    public boolean isParticipantOwner(Long participantId, Long authenticatedUserId){
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));
        return participant.getUser().getId().equals(authenticatedUserId);
    }

    /**
     * Determina si el usuario autenticado puede autoinscribirse en un experimento.
     * Se verifica que el usuario en el DTO coincide con el autenticado,
     * que el experimento existe y está abierto para autoinscripción,
     * y que aún no está inscrito en ese experimentt
     */
    public boolean canJoinExperiment(ParticipantDTO participantDTO, Long authenticatedUserId) {
        if (authenticatedUserId == null) {
            return false;
        }

        // Recuperar el experimento
        Experiment experiment = experimentRepo.findById(participantDTO.getExperimentId())
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + participantDTO.getExperimentId()));

        // Verificar si el experimento está abierto para autoinscripción
        if (!experiment.isOpenForAutoInscription()) {
            throw new RuntimeException("This experiment is closed for auto inscription");
        }

        // Verificar que el usuario no esté ya inscrito en ese experimento
        List<Participant> existentes = participantRepo.findByExperiment(experiment);
        boolean yaInscrito = existentes.stream()
                .anyMatch(p -> p.getUser().getId().equals(authenticatedUserId));

        return !yaInscrito;
    }


    /**
     * Comprueba si el usaurio autentica está inscrito en el experimento
     */
    public boolean isParticipantInExperiment (Long experimentId, Long authenticatedUserId){
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found With ID: " + experimentId));
        return experiment.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(authenticatedUserId)) ;
    }

    /**
     * Verifica si la participación del usuario en el experimento tiene el rol local especificado
     */
    public boolean hasLocaleRole (Long experimentId, Long userId, RoleEnum localRole){
        // Recupera el participante usando el experimento y el usuario
        Optional<Participant> optParticipant = participantRepo.findByExperimentAndUser(
                Experiment.builder().id(experimentId).build(),
                userRepo.findById(userId).orElse(null));
        return optParticipant.filter(p -> p.getRole() == localRole).isPresent();
    }

    /**
     * Verifica si el usuario autenticado es el creador del experimento
     */
    public boolean isCreatorOfExperiment (Long experimentId, Long authenticatedUserId) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        return experiment.getCreatedBy().getId().equals(authenticatedUserId);
    }

    /**
     * Verifica si el usuario autenticado es el creador del Experimetno al que pertenece el participante
     * que se intenta eliminar
     */
    public boolean isCreatorOfParticipantExperiment (Long participantId, Long authenticatedId) {
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));
        // Verificar si el usuario autenticado es el creador del experimento donde participa el usuario
        return participant.getExperiment().getCreatedBy().getId().equals(authenticatedId);

    }
}
