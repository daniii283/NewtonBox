package com.newtonbox.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;


@Entity
@Table(name = "experiments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String variables;

    @Column(name = "open_for_auto_inscription", nullable = false, columnDefinition = "bit(1) default 0")
    private boolean openForAutoInscription = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference(value = "user-experiments")
    private UserEntity createdBy; //

    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "experiment-participants")
    private List<Participant> participants;

    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "experiment-results")
    private List<Result> results;

    @OneToMany(mappedBy = "experiment", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference(value = "experiments-comments")
    private List<Comment> comments;



    public Experiment(String title, String description, String variables, UserEntity createdBy) {
        this.title = title;
        this.description = description;
        this.variables = variables;
        this.createdBy = createdBy;
    }

}
