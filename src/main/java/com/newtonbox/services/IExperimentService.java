package com.newtonbox.services;

import com.newtonbox.dto.ExperimentDTO;
import org.springframework.security.core.Authentication;


import java.util.List;

public interface IExperimentService {

    List<ExperimentDTO> findAll();
    ExperimentDTO findById(Long id);
    List<ExperimentDTO> findByCreatedBy(Long userId);   // Nombre personalizado
    ExperimentDTO save(ExperimentDTO experimentDTO, Authentication authentication);
    ExperimentDTO update(Long id, ExperimentDTO experimentDTO);
    void delete(Long id);
    List<ExperimentDTO> findByCreatedByUsername(String username);

}
