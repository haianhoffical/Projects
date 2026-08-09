package com.dhvestudent.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tutor_subjects")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TutorSubject {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private Tutor tutor;

    @Column(name = "subject_name", nullable = false, length = 100)
    private String subjectName;
}
