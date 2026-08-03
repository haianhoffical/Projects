package com.dhvestudent.repository;

import com.dhvestudent.entity.Tutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, Long> {

    Optional<Tutor> findByUserId(Long userId);

    @Query("SELECT t FROM Tutor t WHERE t.isActive = true " +
           "AND (:subject IS NULL OR EXISTS (SELECT 1 FROM t.subjects s WHERE LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :subject, '%')))) " +
           "AND (:maxPrice IS NULL OR t.hourlyRate <= :maxPrice) " +
           "AND (:search IS NULL OR LOWER(t.user.fullName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(t.bio) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY t.rating DESC")
    Page<Tutor> findActiveTutors(@Param("subject") String subject,
                                 @Param("maxPrice") java.math.BigDecimal maxPrice,
                                 @Param("search") String search,
                                 Pageable pageable);

    long countByIsActiveTrue();
}
