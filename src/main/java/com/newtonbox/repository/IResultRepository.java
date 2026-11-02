package com.newtonbox.repository;

import com.newtonbox.models.Experiment;
import com.newtonbox.models.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IResultRepository extends JpaRepository<Result, Long> {
    List<Result> findByExperiment (Experiment experiment);
}
