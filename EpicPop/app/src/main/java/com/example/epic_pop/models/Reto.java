package com.example.epic_pop.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "retos",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ))
public class Reto {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private String title;
    private String description;
    private String difficulty; // Fácil, Medio, Difícil
    private String status; // Pendiente, En Progreso, Completado
    private String createdDate;
    private String dueDate;
    private int progress; // 0-100
    private String category; // Deportes, Estudio, Ecología, Salud, Creatividad
    private int starsAwarded; // 1-3 estrellas
    private String completedDate;

    public enum Difficulty {
        FACIL("Fácil"),
        MEDIO("Medio"),
        DIFICIL("Difícil");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Category {
        DEPORTES("Deportes", "#6200EE"),
        ESTUDIO("Estudio", "#FF9800"),
        ECOLOGIA("Ecología", "#4CAF50"),
        SALUD("Salud", "#F44336"),
        CREATIVIDAD("Creatividad", "#E91E63");

        private final String displayName;
        private final String color;

        Category(String displayName, String color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
    }

    public enum Status {
        PENDIENTE("Pendiente"),
        EN_PROGRESO("En Progreso"),
        COMPLETADO("Completado");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // Constructor
    public Reto(int userId, String title, String description, String difficulty, String category) {
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.difficulty = difficulty;
        this.category = category;
        this.status = Status.PENDIENTE.getDisplayName();
        this.progress = 0;
        this.starsAwarded = 0;
        this.createdDate = String.valueOf(System.currentTimeMillis()); // Usando timestamp compatible
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getStarsAwarded() { return starsAwarded; }
    public void setStarsAwarded(int starsAwarded) { this.starsAwarded = starsAwarded; }

    public String getCompletedDate() { return completedDate; }
    public void setCompletedDate(String completedDate) { this.completedDate = completedDate; }
}
