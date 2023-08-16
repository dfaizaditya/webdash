package com.kbbukopin.webdash.services.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kbbukopin.webdash.entity.User;
import com.kbbukopin.webdash.exception.BadRequestException;
import com.kbbukopin.webdash.exception.ResourceNotFoundException;
import com.kbbukopin.webdash.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User create(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BadRequestException("Username must be provided");
        }

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new BadRequestException("Username " + user.getUsername() + " is already registered");
        }

        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User edit(User user) {
        if (!StringUtils.hasText(user.getUsername())) {
            throw new BadRequestException("Username must be provided");
        }

        return userRepository.save(user);
    }

    public void deleteById(String id) {
        userRepository.deleteById(id);
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               