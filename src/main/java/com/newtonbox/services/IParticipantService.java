package com.newtonbox.services;

import com.newtonbox.dto.ParticipantDTO;
import com.newtonbox.dto.RoleDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IParticipantService {

    List<ParticipantDTO> findAll();
    ParticipantDTO findById(Long id);
    List<ParticipantDTO> findByExperiment(Long experimentId); // Personalizado
    List<ParticipantDTO> findByUser(Long userId); // Personalizado
    ParticipantDTO assignRoleToParticipant(Long participantId, RoleDTO roleDTO);
    ParticipantDTO save(ParticipantDTO participantDTO,  Authentication authentication);
    void delete(Long id);
}
