package org.example.service;

import org.example.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> findByUsername(String username);
    List<User> findAll();
    User save(User user);
}
