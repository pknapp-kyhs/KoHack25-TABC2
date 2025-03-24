package com.judahharris.kohack2025.model;

import com.judahharris.kohack2025.screen.Displayable;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stored_users") // Use new name because "users" was used for a different project and I didn't want to delete that data or change the table name
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column
    private String hashedPassword; // Hashed password

    @Column(columnDefinition = "TEXT", length = 2048) // Ensures large text storage
    private String context;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Question> questions = new ArrayList<>();

    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question implements Displayable {
        @Column(columnDefinition = "TEXT", length = 1024) // Ensures large text storage
        private String question;

        @Column(columnDefinition = "TEXT", length = 16384) // Ensures large text storage
        private String answer;

        public String display() {
            return question;
        }
    }
}