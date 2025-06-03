package com.chatapp.service;

import com.chatapp.model.User;
import com.chatapp.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
    }

    @Override
    public User getCurrentUser() {
        // This will be implemented with Spring Security context
        return null; // TODO: Implement with SecurityContextHolder
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void updateUserStatus(Long userId, boolean online) {
        User user = findById(userId);
        user.setOnline(online);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void updateLastSeen(Long userId) {
        User user = findById(userId);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void addContact(Long userId, Long contactId) {
        User user = findById(userId);
        User contact = findById(contactId);
        user.getContacts().add(contact);
        userRepository.save(user);
    }

    @Override
    public void removeContact(Long userId, Long contactId) {
        User user = findById(userId);
        User contact = findById(contactId);
        user.getContacts().remove(contact);
        userRepository.save(user);
    }

    @Override
    public List<User> getUserContacts(Long userId) {
        User user = findById(userId);
        return List.copyOf(user.getContacts());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
} 