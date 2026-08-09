package com.dhvestudent.repository;

import com.dhvestudent.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.id = :userId ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByMemberId(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.product.id = :productId AND SIZE(cr.members) = 2")
    Optional<ChatRoom> findByProductAndTwoMembers(@Param("productId") Long productId);
}
