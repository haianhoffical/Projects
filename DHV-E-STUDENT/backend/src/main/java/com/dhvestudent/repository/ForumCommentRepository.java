package com.dhvestudent.repository;

import com.dhvestudent.entity.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ForumCommentRepository extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId);
}
