package com.example.gamestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/api/games")
public class GameStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(GameStoreApplication.class, args);
    }

    @GetMapping
    public List<Map<String, String>> getGames() {
        return List.of(
            Map.of("id", "1", "title", "The Witcher 3: Wild Hunt", "genre", "RPG", "price", "$29.99"),
            Map.of("id", "2", "title", "Cyberpunk 2077", "genre", "Action RPG", "price", "$59.99"),
            Map.of("id", "3", "title", "Hades", "genre", "Rogue-like", "price", "$24.99")
        );
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of("status", "UP", "service", "Game Store API");
    }
}
