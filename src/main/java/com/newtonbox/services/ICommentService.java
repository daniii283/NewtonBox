package com.newtonbox.services;

import com.newtonbox.dto.CommentDTO;
import com.newtonbox.dto.ExperimentDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ICommentService {

    List<CommentDTO> findAll();
    CommentDTO findById(Long id);
    List<CommentDTO> findByExperiment(ExperimentDTO experimentDTO);  // Personalizado
    CommentDTO save(CommentDTO commentDTO, Authentication authentication);
    CommentDTO update(Long id, CommentDTO commentDTO);
    void delete(Long id);
    List<CommentDTO> findByUserId(Long userId);
}
