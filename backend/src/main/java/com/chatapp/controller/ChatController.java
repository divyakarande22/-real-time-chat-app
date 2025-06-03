package com.chatapp.controller;

import com.chatapp.model.ChatMessage;
import com.chatapp.model.ChatRoom;
import com.chatapp.model.User;
import com.chatapp.model.Message;
import com.chatapp.service.ChatService;
import com.chatapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        logger.info("Received message from {}: {}", chatMessage.getSender().getUsername(), chatMessage.getContent());
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        logger.info("Saved message with ID: {}", savedMessage.getId());
        
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoom().getId(), savedMessage);
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        logger.info("User {} joined the chat", chatMessage.getSender().getUsername());
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender().getUsername());
        messagingTemplate.convertAndSend("/topic/room." + chatMessage.getChatRoom().getId(), chatMessage);
    }

    @GetMapping("/rooms/{userId}")
    @ResponseBody
    public List<ChatRoom> getUserChatRooms(@PathVariable Long userId) {
        return chatService.getUserChatRooms(userId);
    }

    @GetMapping("/rooms/{roomId}/messages")
    @ResponseBody
    public Page<ChatMessage> getRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return chatService.getRoomMessages(roomId, PageRequest.of(page, size, Sort.by("timestamp").descending()));
    }

    @PostMapping("/rooms/private")
    @ResponseBody
    public ChatRoom createPrivateChat(@RequestBody List<User> users) {
        if (users.size() != 2) {
            throw new IllegalArgumentException("Private chat requires exactly 2 users");
        }
        return chatService.createPrivateChat(users.get(0), users.get(1));
    }

    @PostMapping("/rooms/group")
    @ResponseBody
    public ChatRoom createGroupChat(@RequestBody ChatRoom chatRoom) {
        return chatService.createGroupChat(chatRoom.getName(), chatRoom.getParticipants());
    }

    @PostMapping("/rooms/{roomId}/read")
    @ResponseBody
    public void markMessagesAsRead(@PathVariable Long roomId, @RequestParam Long userId) {
        chatService.markMessagesAsRead(roomId, userId);
    }

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        String username = headerAccessor.getUser().getName();
        User sender = userService.findByUsername(username);
        message.setSender(sender);
        
        Message savedMessage = chatService.saveMessage(message);
        
        messagingTemplate.convertAndSendToUser(
            message.getReceiver().getUsername(),
            "/queue/messages",
            savedMessage
        );
    }

    @GetMapping("/messages/{userId}")
    public List<Message> getChatMessages(@PathVariable Long userId) {
        User currentUser = userService.getCurrentUser();
        User otherUser = userService.findById(userId);
        return chatService.getChatMessages(currentUser, otherUser);
    }

    @GetMapping("/unread")
    public List<Message> getUnreadMessages() {
        User currentUser = userService.getCurrentUser();
        return chatService.getUnreadMessages(currentUser);
    }

    @PutMapping("/messages/{messageId}/read")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatService.markMessageAsRead(messageId);
    }
} 