package com.newtonbox.controllers;

import com.newtonbox.services.Impl.ResultService;
import com.newtonbox.dto.ExperimentDTO;
import com.newtonbox.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/results")
@PreAuthorize("isAuthenticated()")
public class ResultController {

    @Autowired
    private ResultService resultService;

    // ------------------ Métodos CRUD ------------------

    // Obtener todos los resultados de un proyecto
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ResultDTO>> findAll() {
        List<ResultDTO> results = resultService.findAll();
        return ResponseEntity.ok(results);
    }

    // Obtener un resultado por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @resultValidator.canAccessResult(#id, authentication.principal.id)")
    public ResponseEntity<ResultDTO> findById(@PathVariable Long id) {
        ResultDTO resultDTO = resultService.findById(id);
        return ResponseEntity.ok(resultDTO);
    }

    // Crear un resultado
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @resultValidator.canCreateResultInExperiment(#resultDTO.experimentId, authentication.principal.id)")
    public ResponseEntity<ResultDTO> save(@RequestBody ResultDTO resultDTO, Authentication authentication) {
        ResultDTO saved = resultService.save(resultDTO, authentication);
        return ResponseEntity.ok(saved);
    }

    // Actualizar un resultado
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @resultValidator.canUpdateResult(#id, authentication.principal.id)")
    public ResponseEntity<ResultDTO> update(@PathVariable Long id, @RequestBody ResultDTO resultDTO) {
        ResultDTO updated = resultService.update(id, resultDTO);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    // Borrar un resultado
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @resultValidator.canDeleteResult(#id, authentication.principal.id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        resultService.delete(id);
        System.out.println();
        return ResponseEntity.noContent().build();
    }

    // ------------------ Métodos Personalizado ------------------

    // Buscar resultados por un experimento en concreto
    @GetMapping("/byExperiment/{experimentId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @resultValidator.canAccessResultOfExperiment(#experimentId, authentication.principal.id)")
    public ResponseEntity<List<ResultDTO>> findByExperiment(@PathVariable Long experimentId) {
        ExperimentDTO experimentDTO = new ExperimentDTO();
        experimentDTO.setId(experimentId);

        List<ResultDTO> results = resultService.findByExperiment(experimentDTO);
        return results.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(results);
    }
}
