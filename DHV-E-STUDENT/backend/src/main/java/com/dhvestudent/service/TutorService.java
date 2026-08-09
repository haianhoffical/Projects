package com.dhvestudent.service;

import com.dhvestudent.dto.*;
import com.dhvestudent.entity.*;
import com.dhvestudent.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
public class TutorService {

    @Autowired private TutorRepository tutorRepository;
    @Autowired private TutorSubjectRepository subjectRepository;
    @Autowired private UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<Tutor> getTutors(String subject, BigDecimal maxPrice, String search, String sort, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if ("price-asc".equals(sort)) pageable = PageRequest.of(page, size, Sort.by("hourlyRate").ascending());
        if ("price-desc".equals(sort)) pageable = PageRequest.of(page, size, Sort.by("hourlyRate").descending());
        if ("reviews".equals(sort)) pageable = PageRequest.of(page, size, Sort.by("reviewCount").descending());

        Page<Tutor> result = tutorRepository.findActiveTutors(subject, maxPrice, search, pageable);
        return PageResponse.<Tutor>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .last(result.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public Tutor getTutorById(Long id) {
        return tutorRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy gia sư"));
    }

    @Transactional
    public Tutor registerTutor(Long userId, Tutor tutor, List<String> subjects) {
        User user = userRepository.findById(userId).orElseThrow();
        tutor.setUser(user);
        tutor.setIsActive(true);
        Tutor saved = tutorRepository.save(tutor);

        if (subjects != null) {
            for (String s : subjects) {
                TutorSubject ts = TutorSubject.builder().tutor(saved).subjectName(s).build();
                subjectRepository.save(ts);
                saved.getSubjects().add(ts);
            }
        }
        return saved;
    }

    @Transactional(readOnly = true)
    public long countActiveTutors() {
        return tutorRepository.countByIsActiveTrue();
    }
}
