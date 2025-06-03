package com.chatapp.repository;

import com.chatapp.model.Message;
import com.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySenderAndReceiverOrderByTimestampDesc(User sender, User receiver);
    
    @Query("SELECT m FROM Message m WHERE (m.sender = ?1 AND m.receiver = ?2) OR (m.sender = ?2 AND m.receiver = ?1) ORDER BY m.timestamp DESC")
    List<Message> findChatMessages(User user1, User user2);
    
    List<Message> findByReceiverAndReadFalse(User receiver);
} 