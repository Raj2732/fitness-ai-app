package com.example.fitnessaiwrapper.fitnessai.repository;

import com.example.fitnessaiwrapper.fitnessai.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserId(String userId);

    @Query("SELECT c FROM ChatMessage c WHERE c.userRating IS NOT NULL ORDER BY c.timestamp DESC")
    List<ChatMessage> findRatedMessages();

    @Query("SELECT c FROM ChatMessage c WHERE c.userMessage LIKE %:keyword% OR c.aiResponse LIKE %:keyword%")
    List<ChatMessage> searchMessages(@Param("keyword") String keyword);

    @Query("SELECT c FROM ChatMessage c WHERE c.userId = :userId ORDER BY c.timestamp DESC")
    List<ChatMessage> findRecentByUserId(@Param("userId") String userId);
}
