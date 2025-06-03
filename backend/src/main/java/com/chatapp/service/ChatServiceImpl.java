package com.chatapp.service;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.Message;
import com.chatapp.model.User;
import com.chatapp.repository.ChatMessageRepository;
import com.chatapp.repository.ChatRoomRepository;
import com.chatapp.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    public ChatServiceImpl(MessageRepository messageRepository,
                          ChatMessageRepository chatMessageRepository,
                          ChatRoomRepository chatRoomRepository,
                          UserService userService) {
        this.messageRepository = messageRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.userService = userService;
    }

    // Message methods
    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    @Override
    public List<Message> getChatMessages(User user1, User user2) {
        return messageRepository.findChatMessages(user1, user2);
    }

    @Override
    public List<Message> getUnreadMessages(User user) {
        return messageRepository.findByReceiverAndReadFalse(user);
    }

    @Override
    public void markMessageAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        messageRepository.save(message);
    }

    // ChatMessage methods
    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    @Override
    public Page<ChatMessage> getRoomMessages(Long chatRoomId, Pageable pageable) {
        return chatMessageRepository.findByChatRoomId(chatRoomId, pageable);
    }

    @Override
    public List<ChatRoom> getUserChatRooms(Long userId) {
        User user = userService.findById(userId);
        return chatRoomRepository.findByParticipantsContaining(user);
    }

    @Override
    public Optional<ChatRoom> getChatRoom(Long chatRoomId, Long userId) {
        User user = userService.findById(userId);
        return chatRoomRepository.findByIdAndParticipantsContaining(chatRoomId, user);
    }

    @Override
    public ChatRoom createPrivateChat(User user1, User user2) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findPrivateChatRoom(user1, user2);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(user1.getUsername() + " & " + user2.getUsername());
        chatRoom.getParticipants().add(user1);
        chatRoom.getParticipants().add(user2);
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public ChatRoom createGroupChat(String name, Set<User> participants) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.getParticipants().addAll(participants);
        return chatRoomRepository.save(chatRoom);
    }

    @Override
    public void markMessagesAsRead(Long chatRoomId, Long userId) {
        User user = userService.findById(userId);
        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomIdAndReceiverAndReadFalse(chatRoomId, user);
        unreadMessages.forEach(message -> {
            message.setRead(true);
            chatMessageRepository.save(message);
        });
    }
} 