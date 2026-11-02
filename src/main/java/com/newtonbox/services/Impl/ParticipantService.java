package com.newtonbox.services.Impl;

import com.newtonbox.models.*;
import com.newtonbox.repository.IExperimentRepository;
import com.newtonbox.repository.IParticipantRepository;
import com.newtonbox.repository.IUserRepository;
import com.newtonbox.security.CustomUserDetails;
import com.newtonbox.services.IParticipantService;
import com.newtonbox.dto.ParticipantDTO;
import com.newtonbox.dto.RoleDTO;
import com.newtonbox.mapper.ParticipantMapper;
import com.newtonbox.utils.RoleEnum;
import com.newtonbox.validators.ParticipantValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("participantService")
public class ParticipantService implements IParticipantService {

    @Autowired
    private IParticipantRepository participantRepo;

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    @Autowired
    private ParticipantValidator participantValidator;

    @Override
    public List<ParticipantDTO> findAll() {
        return participantRepo.findAll().stream()
                .map(ParticipantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipantDTO findById(Long id) {
        Participant participant = participantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID:" + id));
        return ParticipantMapper.toDTO(participant);
    }

    // --- Personalizado--
    @Override
    public List<ParticipantDTO> findByExperiment(Long experimentId) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        return participantRepo.findByExperiment(experiment).stream()
                .map(ParticipantMapper::toDTO)
                .collect(Collectors.toList());
    }

    // --- Personalizado--
    @Override
    public List<ParticipantDTO> findByUser(Long userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Participants not found with ID: " + userId));
        return participantRepo.findByUser(user).stream()
                .map(ParticipantMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParticipantDTO assignRoleToParticipant(Long participantId, RoleDTO roleDTO) {
        // Recuperar el participante existente
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));

        // Validar y asignar el rol desde RoleDTO
        if(roleDTO.getRoleEnum() == null || roleDTO.getRoleEnum().isEmpty()){
            throw new RuntimeException("Role is required");
        }

        try {
            participant.setRole(RoleEnum.valueOf(roleDTO.getRoleEnum().trim()));
        }catch (IllegalArgumentException e){
            throw new RuntimeException("Invalid role provided: " + roleDTO.getRoleEnum());
        }

        // Guardar los cambios y devolver el DTO actualizado
        Participant updated = participantRepo.save(participant);
        return ParticipantMapper.toDTO(updated);
    }

    @Override
    public ParticipantDTO save(ParticipantDTO participantDTO, Authentication authentication) {
        // Obtener el usuario autenticado
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long authenticatedUserId = userDetails.getId();

        // Convertir el DTO a entidad (sin asignar el experimento aún)
        Participant participant = ParticipantMapper.toEntity(participantDTO);

        // Buscar el usuario autenticado en la base de datos
        UserEntity user = userRepo.findById(authenticatedUserId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + authenticatedUserId));
        participant.setUser(user);

        // Verificar que el experimentId viene en el DTO
        if (participantDTO.getExperimentId() == null) {
            throw new RuntimeException("Experiment ID is required to create a participant.");
        }

        // Recuperar la entidad Experiment desde la base de datos
        Experiment experiment = experimentRepo.findById(participantDTO.getExperimentId())
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + participantDTO.getExperimentId()));
        participant.setExperiment(experiment);

        // Verificar autoinscripción
        if (!participantValidator.canJoinExperiment(participantDTO, authenticatedUserId)) {
            throw new RuntimeException("Cannot join experiment: conditions not met");
        }

        // Asignar rol por defecto si no se ha proporcionado
        String roleString = Optional.ofNullable(participantDTO.getRole()).orElse("REVIEWER").trim();
        try {
            participant.setRole(RoleEnum.valueOf(roleString));
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role provided: " + roleString);
        }

        // Guardar el participante y devolver el DTO resultante
        Participant saved = participantRepo.save(participant);
        return ParticipantMapper.toDTO(saved);
    }



    @Override
    public void delete(Long id) {
        Participant existing = participantRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + id));
        participantRepo.delete(existing);
    }

}
