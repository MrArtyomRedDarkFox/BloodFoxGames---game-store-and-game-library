package com.example.gamestore.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.example.gamestore.dto.*;
import com.example.gamestore.model.*;
import com.example.gamestore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();
    private final RestTemplate restTemplate = new RestTemplate();
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
        return userRepository.save(
    new User(
        dto.getUsername(),
        dto.getEmail(),
        passwordEncoder.encode(dto.getPassword())
    )
);
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
try {
    restTemplate.postForEntity(
        "http://notifications-service:8082/api/v1/notifications",
        java.util.Map.of(
            "user_id", userId,
            "message", "Game purchased: " + game.getTitle()
        ),
        String.class
    );
} catch (org.springframework.web.client.RestClientException e) {
    System.err.println("Notification failed: " + e.getMessage());
}
    }

    public Set<Game> getLibrary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return user.getLibrary();
    }
}
