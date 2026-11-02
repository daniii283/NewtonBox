package com.newtonbox.services.Impl;

import com.newtonbox.models.*;
import com.newtonbox.repository.*;
import com.newtonbox.security.CustomUserDetails;
import com.newtonbox.services.IExperimentService;
import com.newtonbox.dto.ExperimentDTO;
import com.newtonbox.mapper.CommentMapper;
import com.newtonbox.mapper.ExperimentMapper;
import com.newtonbox.mapper.ResultMapper;
import com.newtonbox.utils.RoleEnum;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("experimentService")
public class ExperimentService implements IExperimentService {

    @Autowired
    private IExperimentRepository experimentRepo;

    @Autowired
    private IUserRepository userRepo;

    @Autowired
    private IParticipantRepository participantRepo;

    @Autowired
    private ICommentRepository commentRepo;

    @Autowired
    private IResultRepository resultRepo;

    @Override
    public List<ExperimentDTO> findAll() {
        return experimentRepo.findAll().stream()
                .map(ExperimentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExperimentDTO findById(Long id) {
        Experiment experiment = experimentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID:" + id));

        return ExperimentMapper.toDTO(experiment);
    }

    // --- Personalizado--
    @Override
    public List<ExperimentDTO> findByCreatedBy(Long userId) {
        UserEntity createdBy = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return experimentRepo.findByCreatedBy(createdBy).stream()
                .map(ExperimentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExperimentDTO save(ExperimentDTO experimentDTO, Authentication authentication) {
        // Convertir el DTO a entidad (ignorando el campo createdBy del DTO)
        Experiment experiment = ExperimentMapper.toEntity(experimentDTO);


        // Obtener la información del usuario autenticado (CustomeUserDetails)
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Buscar la entidad del usuario por username
        UserEntity creator = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));

        // Asignar el creador obtenido al experimento
        experiment.setCreatedBy(creator);

        // Guardar el experimento para obtener su ID
        Experiment savedExperiment = experimentRepo.save(experiment);

        // Procesar participantes
        List<Participant> participants = new ArrayList<>();
        if (experimentDTO.getParticipants() != null) {
            experimentDTO.getParticipants().forEach(participantDTO -> {
                Participant participant = new Participant();
                participant.setId(participantDTO.getId());

                // Buscar al usuario por su 'username' en el repositorio
                UserEntity user = userRepo.findByUsername(participantDTO.getUsername())
                        .orElseThrow(() -> new RuntimeException(
                                "User not found with username: " + participantDTO.getUsername()));

                participant.setUser(user);

                // Asignar el rol (si no se recibe, podrías asignar uno por defecto)
                if (participantDTO.getRole() != null && !participantDTO.getRole().isEmpty()) {
                    participant.setRole(RoleEnum.valueOf(participantDTO.getRole().trim()));
                } else {
                    throw new RuntimeException("Participant role is required.");
                }

                // Asignar el experimento creado al participante
                participant.setExperiment(savedExperiment);
                participants.add(participantRepo.save(participant));
            });
        }


        // Procesar comentarios
        List<Comment> comments = new ArrayList<>();
        if (experimentDTO.getComments() != null) {
            experimentDTO.getComments().forEach(commentDTO -> {
                Comment comment = CommentMapper.toEntity(commentDTO);
                comment.setExperiment(savedExperiment);
                comments.add(commentRepo.save(comment));
            });
        }

        // Procesar resultados
        List<Result> results = new ArrayList<>();
        if (experimentDTO.getResults() != null) {
            experimentDTO.getResults().forEach(resultDTO -> {
                Result result = ResultMapper.toEntity(resultDTO);
                result.setExperiment(savedExperiment);
                results.add(resultRepo.save(result));
            });
        }

        // Asignar las listas procesadas al experimento
        savedExperiment.setParticipants(participants);
        savedExperiment.setComments(comments);
        savedExperiment.setResults(results);

        // Actualizar el experimento con las asociaciones
        Experiment updatedExperiment = experimentRepo.save(savedExperiment);

        return ExperimentMapper.toDTO(updatedExperiment);
    }

    @Override
    public ExperimentDTO update(Long id, ExperimentDTO experimentDTO) {
        Experiment existingExperiment = experimentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + id));

        // Actualizar solo atributos base (sin modificar las colecciones)
        existingExperiment.setTitle(experimentDTO.getTitle());
        existingExperiment.setDescription(experimentDTO.getDescription());
        existingExperiment.setVariables(experimentDTO.getVariables());

        Experiment updatedExperiment = experimentRepo.save(existingExperiment);
        return ExperimentMapper.toDTO(updatedExperiment);
    }

    @Override
    public void delete(Long id) {
        Experiment experiment = experimentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + id));
        experimentRepo.delete(experiment);
    }

    @Override
    public List<ExperimentDTO> findByCreatedByUsername(String username) {
        List<Experiment> experiments = experimentRepo.findByCreatedByUsername(username);
        return experiments.stream()
                .map(ExperimentMapper::toDTO)
                .collect(Collectors.toList());
    }



    // Métodos auxiliares

    public boolean isCreator(Long experimentId, String username) {
        Experiment exp = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        return exp.getCreatedBy().getUsername().equals(username);
    }

    public boolean isUserAllowed(Long experimentId, String username) {
        Experiment exp = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));
        boolean isCreator = exp.getCreatedBy().getUsername().equals(username);
        boolean isParticipant = exp.getParticipants().stream()
                .anyMatch(p -> p.getUser().getUsername().equals(username));
        return isCreator || isParticipant;
    }

    private RoleEnum assignRoleBasedOnUser(UserEntity user) {
        if (user == null) {
            throw new RuntimeException("Error: El usuario recibido es NULL.");
        }

        System.out.println("Asignando rol para usuario: " + user.getUsername());

        // Forzar la carga de roles manualmente
        user.getRoles().size();

        System.out.println("Roles del usuario: " + user.getRoles());

        if (user.getRoles().isEmpty()) {
            throw new RuntimeException("El usuario " + user.getUsername() + " no tiene roles asignados.");
        }

        // Obtener el primer rol del usuario
        Role role = user.getRoles().iterator().next();

        // Devolver el RoleEnum correspondiente
        return role.getRoleEnum();
    }
    public boolean hasPermission(Long experimentId, String username, String permission) {
        Experiment experiment = experimentRepo.findById(experimentId)
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + experimentId));

        // Si el usuario es el creador, le otorgamos permisos completos en el experimento.
        if (experiment.getCreatedBy().getUsername().equalsIgnoreCase(username)) {
            return true;
        }

        // Si el usuario es participante, recorremos sus roles para verificar el permiso.
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

    // Verifica si el usuario que hace la peticion es el creador del experimento
    public boolean isCreatorOfParticipant(Long participantId, Long userId) {
        Participant participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found with ID: " + participantId));

        // Verificamos si el creador del experimento es el usuario autenticado
        return participant.getExperiment().getCreatedBy().getId().equals(userId);
    }


}
