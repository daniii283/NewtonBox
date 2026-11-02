package com.newtonbox.services.Impl;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.Result;
import com.newtonbox.models.UserEntity;
import com.newtonbox.repository.IExperimentRepository;
import com.newtonbox.repository.IResultRepository;
import com.newtonbox.repository.IUserRepository;
import com.newtonbox.security.CustomUserDetails;
import com.newtonbox.services.IResultService;
import com.newtonbox.dto.ExperimentDTO;
import com.newtonbox.dto.ResultDTO;
import com.newtonbox.mapper.ExperimentMapper;
import com.newtonbox.mapper.ResultMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultService implements IResultService {

    @Autowired
    private IResultRepository resultRepo;

    @Autowired
    private IExperimentRepository experimentRepo;

    @Autowired
    private IUserRepository userRepo;

    @Override
    public List<ResultDTO> findAll() {

        return resultRepo.findAll().stream()
                .map(ResultMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResultDTO findById(Long id) {
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found with ID: " + id));
        return ResultMapper.toDTO(result);
    }

    // --- Personalizado--
    @Override
    public List<ResultDTO> findByExperiment(ExperimentDTO experimentDTO) {
        Experiment experiment = ExperimentMapper.toEntity(experimentDTO);
        return resultRepo.findByExperiment(experiment).stream()
                .map(ResultMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ResultDTO save(ResultDTO resultDTO, Authentication authentication) {
        Result result = ResultMapper.toEntity(resultDTO);

        // Verificar que se proporcione el experimentId
        if (resultDTO.getExperimentId() == null) {
            throw new RuntimeException("Experiment ID is required to create a result.");
        }

        // Recuperar la entidad Experiment desde la base de datos
        Experiment experiment = experimentRepo.findById(resultDTO.getExperimentId())
                .orElseThrow(() -> new RuntimeException("Experiment not found with ID: " + resultDTO.getExperimentId()));

        // (Opcional) Verificar que el título proporcionado coincide con el título del experimento
        if (resultDTO.getExperimentTitle() != null &&
                !resultDTO.getExperimentTitle().equalsIgnoreCase(experiment.getTitle())) {
            throw new RuntimeException("Experiment title does not match for ID: " + resultDTO.getExperimentId());
        }

        // Obtener el usuario autenticado
        Long userId = ((CustomUserDetails) authentication.getPrincipal()).getId();
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Asignar el experimento al resultado
        result.setExperiment(experiment);

        // Establece el creador del resultado
        result.setCreatedBy(user);

        Result saved = resultRepo.save(result);
        return ResultMapper.toDTO(saved);
    }

    @Override
    public ResultDTO update(Long id, ResultDTO resultDTO) {
        Result existingResult = resultRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found with ID: " + id));
        // Actualizamos el campo data
        if (resultDTO.getData() != null) {
            existingResult.setData(resultDTO.getData());
        }

        Result updatedResult = resultRepo.save(existingResult);
        return ResultMapper.toDTO(updatedResult);
    }

    @Override
    public void delete(Long id) {
        Result result = resultRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Result not found with ID: " + id));
        resultRepo.delete(result);
    }
}
