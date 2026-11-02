package com.newtonbox.services.Impl;

import com.newtonbox.exceptions.badRequest.MissingFieldException;
import com.newtonbox.exceptions.notFound.CommentNotFoundException;
import com.newtonbox.exceptions.notFound.ExperimentNotFoundException;
import com.newtonbox.exceptions.notFound.UserNotFoundException;
import com.newtonbox.models.Comment;
import com.newtonbox.models.Experiment;
import com.newtonbox.models.UserEntity;
import com.newtonbox.repository.ICommentRepository;
import com.newtonbox.repository.IExperimentRepository;
import com.newtonbox.repository.IUserRepository;
import com.newtonbox.security.CustomUserDetails;
import com.newtonbox.services.ICommentService;
import com.newtonbox.dto.CommentDTO;
import com.newtonbox.dto.ExperimentDTO;
import com.newtonbox.mapper.CommentMapper;
import com.newtonbox.mapper.ExperimentMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService implements ICommentService {

    @Autowired
    private ICommentRepository commentRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    @Autowired
    private IUserRepository userRepo;

    @Override
    public List<CommentDTO> findAll() {
        return commentRepo.findAll().stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDTO findById(Long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        return CommentMapper.toDTO(comment);
    }

    // --- Personalizado--
    @Override
    public List<CommentDTO> findByExperiment(ExperimentDTO experimentDTO) {
        Experiment experiment = ExperimentMapper.toEntity(experimentDTO);
        return commentRepo.findByExperiment(experiment).stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDTO save(CommentDTO commentDTO, Authentication authentication) {
        // Convertir el DTO a entidad
        Comment comment = CommentMapper.toEntity(commentDTO);

        // Verificar que se envíe el experimentId
        if (commentDTO.getExperimentId() == null) {
            throw new MissingFieldException();
        }

        // Recuperar el experimento a partir del experimentId
        Experiment experiment = experimentRepo.findById(commentDTO.getExperimentId())
                .orElseThrow(() -> new ExperimentNotFoundException(commentDTO.getExperimentId()));
        comment.setExperiment(experiment);

        // Obtener la información del usuario autenticado a través de CustomeUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        // Buscar el UserEntity basado en el ID del usuario autenticado
        UserEntity user = userRepo.findById(userDetails.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userDetails.getId()));
        // Asignar el usuario autenticado como autor del comentario
        comment.setUser(user);

        // Guardar el comentario en la base de datos
        Comment savedComment = commentRepo.save(comment);

        return CommentMapper.toDTO(savedComment);
    }


    @Override
    public CommentDTO update(Long id, CommentDTO commentDTO) {
        Experiment experiment = experimentRepo.findById(commentDTO.getExperimentId())
                .orElseThrow(() -> new ExperimentNotFoundException(commentDTO.getExperimentId()));
        Comment existingComment = commentRepo.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));

        existingComment.setContent(commentDTO.getContent());
        Comment savedComment = commentRepo.save(existingComment);
        return CommentMapper.toDTO(savedComment);
    }

    @Override
    public void delete(Long id) {
        Comment comment = commentRepo.findById(id)
                .orElseThrow(() -> new CommentNotFoundException(id));
        commentRepo.delete(comment);
    }

    @Override
    public List<CommentDTO> findByUserId(Long userId) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        return commentRepo.findByUser(user).stream()
                .map(CommentMapper::toDTO)
                .collect(Collectors.toList());
    }
}
