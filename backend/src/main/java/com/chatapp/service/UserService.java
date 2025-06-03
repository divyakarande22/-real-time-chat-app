package com.chatapp.service;

import com.chatapp.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import java.util.List;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    User findById(Long id);
    User getCurrentUser();
    User save(User user);
    List<User> findAll();
    void updateUserStatus(Long userId, boolean online);
    void updateLastSeen(Long userId);
    void addContact(Long userId, Long contactId);
    void removeContact(Long userId, Long contactId);
    List<User> getUserContacts(Long userId);
    boolean existsByUsername(String username);
    boolean existsByPhoneNumber(String phoneNumber);
} 