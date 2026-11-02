package com.newtonbox.mapper;

import com.newtonbox.models.Participant;
import com.newtonbox.models.UserEntity;
import com.newtonbox.dto.ParticipantDTO;
import com.newtonbox.utils.RoleEnum;

public class ParticipantMapper {

    public static ParticipantDTO toDTO(Participant participant) {
        if (participant == null) return null;

        return ParticipantDTO.builder()
                .id(participant.getId())
                .username(participant.getUser().getUsername())
                .role(participant.getRole() != null ? participant.getRole().name() : null)
                // Asignamos el ID del experimento
                .experimentId(
                        participant.getExperiment() != null
                                ? participant.getExperiment().getId()
                                : null
                )
                .build();
    }

    public static Participant toEntity(ParticipantDTO participantDTO) {
        if (participantDTO == null) return null;

        Participant participant = new Participant();
        participant.setId(participantDTO.getId());

        if (participantDTO.getUsername() != null) {
            UserEntity user = new UserEntity();
            user.setUsername(participantDTO.getUsername());
            participant.setUser(user);
        }

        // Manejo de excepciones seguro al convertir `String` a `Enum`
        if (participantDTO.getRole() != null) {
            participant.setRole(RoleEnum.valueOf(participantDTO.getRole().trim())); // O RoleEnum si aplica
        }
        return participant;
    }
}



