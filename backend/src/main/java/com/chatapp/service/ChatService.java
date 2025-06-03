package com.chatapp.service;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatService {
    // ChatMessage methods
    ChatMessage saveMessage(ChatMessage message);
    Page<ChatMessage> getRoomMessages(Long chatRoomId, Pageable pageable);
    List<ChatRoom> getUserChatRooms(Long userId);
    Optional<ChatRoom> getChatRoom(Long chatRoomId, Long userId);
    ChatRoom createPrivateChat(User user1, User user2);
    ChatRoom createGroupChat(String name, Set<User> participants);
    void markMessagesAsRead(Long chatRoomId, Long userId);

    // Message methods
    Message saveMessage(Message message);
    List<Message> getChatMessages(User user1, User user2);
    List<Message> getUnreadMessages(User user);
    void markMessageAsRead(Long messageId);
} 