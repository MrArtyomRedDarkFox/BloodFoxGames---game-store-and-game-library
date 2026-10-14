package com.example.gamestore.service;

import com.example.gamestore.dto.*;
import com.example.gamestore.model.*;
import com.example.gamestore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public UserService(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public User register(RegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Имя пользователя занято");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email занят");
        }
        return userRepository.save(new User(dto.getUsername(), dto.getEmail(), dto.getPassword()));
    }

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail(), user.getLibrary());
    }

    @Transactional
    public void buyGame(Long userId, Long gameId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Игра не найдена"));

        user.getLibrary().add(game);
        userRepository.save(user);
    }

    public Set<Game> getLibrary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getLibrary();
    }
}
