package com.dhvestudent.repository;

import com.dhvestudent.entity.TutorSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TutorSubjectRepository extends JpaRepository<TutorSubject, Long> {
    List<TutorSubject> findByTutorId(Long tutorId);
}
