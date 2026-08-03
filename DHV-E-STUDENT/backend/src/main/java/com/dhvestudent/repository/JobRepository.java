package com.dhvestudent.repository;

import com.dhvestudent.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("SELECT j FROM Job j WHERE j.isActive = true " +
           "AND (:type IS NULL OR j.jobType = :type) " +
           "AND (:search IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "     OR LOWER(j.company) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY j.createdAt DESC")
    Page<Job> findActiveJobs(@Param("type") Job.JobType type,
                             @Param("search") String search,
                             Pageable pageable);

    long countByIsActiveTrue();
}
