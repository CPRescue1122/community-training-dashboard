package com.rescue.communitytraining.services;

import com.rescue.communitytraining.entity.User;
import com.rescue.communitytraining.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

//    @Autowired
//    private PasswordEncoder passwordEncoder; // Optional, if using encryption

    public void save(User user) {
        // Optional: Encrypt password
//        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPassword(user.getPassword());
        userRepository.save(user);
    }

    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username);
        if (user != null && user.getPassword().equals(rawPassword)) {
            return user;
        }
        return null;
    }
}

