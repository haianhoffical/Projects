package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class JobService {

    @Autowired private JobRepository jobRepository;
    @Autowired private UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<Job> getJobs(String type, String search, int page, int size) {
        Job.JobType jobType = null;
        if (type != null && !type.isEmpty()) {
            try { jobType = Job.JobType.valueOf(type.toUpperCase().replace("-", "_")); } catch (Exception ignored) {}
        }
        Page<Job> result = jobRepository.findActiveJobs(jobType, search, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return PageResponse.<Job>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy việc làm"));
    }

    @Transactional
    public Job createJob(Long posterId, Job job) {
        User poster = userRepository.findById(posterId).orElseThrow();
        job.setPoster(poster);
        job.setIsActive(true);
        return jobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public long countActiveJobs() {
        return jobRepository.countByIsActiveTrue();
    }
}
