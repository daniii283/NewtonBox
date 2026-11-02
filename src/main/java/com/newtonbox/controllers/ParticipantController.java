package com.newtonbox.controllers;

import com.newtonbox.services.Impl.ParticipantService;
import com.newtonbox.dto.ParticipantDTO;
import com.newtonbox.dto.RoleDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participants")
@PreAuthorize("isAuthenticated()")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    // ------------------ Métodos CRUD ------------------

    // Obtener todos los participantes de un proyecto
    @GetMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ParticipantDTO>> findAll() {
        List<ParticipantDTO> participants = participantService.findAll();
        return ResponseEntity.ok(participants);
    }

    // Obtener un participante por ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @participantValidator.isParticipantOwner(#id, authentication.principal.id)")
    public ResponseEntity<ParticipantDTO> findById(@PathVariable Long id) {
        ParticipantDTO participantDTO = participantService.findById(id);
        return ResponseEntity.ok(participantDTO);
    }

    // Crear un participante  (sólo ADMIN o mediante un proceso controlado, por ejemplo, si se permite autoinscripción)
    @PostMapping("/save")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @participantValidator.canJoinExperiment(#participantDTO, authentication.principal.id)")
    public ResponseEntity<ParticipantDTO> save(@RequestBody ParticipantDTO participantDTO, Authentication authentication) {
        ParticipantDTO saved = participantService.save(participantDTO, authentication);
        return ResponseEntity.ok(saved);
    }

    // Endpoint para asignar o actualizar el rol de un participante
    @PutMapping("/{id}/assign-role")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @participantValidator.isCreatorOfParticipant(#id, authentication.principal.id)")
    public ResponseEntity<ParticipantDTO> assignRole(@PathVariable Long id, @RequestBody RoleDTO roleDTO) {
        ParticipantDTO updatedParticipant = participantService.assignRoleToParticipant(id, roleDTO);
        return ResponseEntity.ok(updatedParticipant);
    }


    // Borrar un participante de un proyecto (sólo el propio participante o ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @participantValidator.isParticipantOwner(#id, authentication.principal.id) or @participantValidator.isCreatorOfParticipantExperiment(#id, authentication.principal.id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        participantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ Métodos Personalizados ------------------

    // Buscar participantes de un experimento (accesible a ADMIN o a usuarios involucrados en ese experimento)
    @GetMapping("/experiment/{expId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or @participantValidator.isParticipantInExperiment(#expId, authentication.principal.id) or @participantValidator.isCreatorOfExperiment(#expId, authentication.principal.id)")
    public ResponseEntity<List<ParticipantDTO>> findByExperiment(@PathVariable Long expId) {
        List<ParticipantDTO> participants = participantService.findByExperiment(expId);
        return participants.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(participants);
    }

    // Buscar participantes por usuario(qué experimentos y roles asume un usuario (sólo el mismo usuario o ADMIN))
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<List<ParticipantDTO>> findByUser(@PathVariable Long userId) {
        List<ParticipantDTO> participants = participantService.findByUser(userId);
        return participants.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(participants);
    }
}