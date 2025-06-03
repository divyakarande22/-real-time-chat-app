package com.chatapp.repository;

import com.chatapp.model.ChatRoom;
import com.chatapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByParticipantsContaining(User user);
    Optional<ChatRoom> findByIdAndParticipantsContaining(Long id, User user);
    Optional<ChatRoom> findPrivateChatRoom(User user1, User user2);
} 