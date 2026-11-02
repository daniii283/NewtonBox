package com.newtonbox.repository;

import com.newtonbox.models.Comment;
import com.newtonbox.models.Experiment;
import com.newtonbox.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ICommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByExperiment(Experiment experiment);
    List<Comment> findByUser(UserEntity user);

}
