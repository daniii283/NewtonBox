package com.newtonbox.services;

import com.newtonbox.dto.ExperimentDTO;
import com.newtonbox.dto.ResultDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface IResultService {

    List<ResultDTO> findAll();
    ResultDTO findById(Long id);
    List<ResultDTO> findByExperiment(ExperimentDTO experimentDTO);  // Personalizado
    ResultDTO save(ResultDTO result, Authentication authentication);
    ResultDTO update(Long id, ResultDTO resultDTO);
    void delete(Long id);
}
