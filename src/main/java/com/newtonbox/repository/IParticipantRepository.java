package com.newtonbox.repository;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.Participant;
import com.newtonbox.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface IParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByUser(UserEntity user);
    List<Participant> findByExperiment(Experiment experiment);
    Optional<Participant> findByExperimentAndUser(Experiment experiment, UserEntity user);
}

