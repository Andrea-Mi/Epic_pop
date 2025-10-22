package com.example.epic_pop.models;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "logros",
        foreignKeys = @ForeignKey(
                entity = Usuario.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE
        ))
public class Logro {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int userId;
    private String achievementType;
    private String dateEarned;
    private String description;
    private String title;
    private String iconUrl;
    private int pointsAwarded;

    public enum AchievementType {
        PRIMERA_RACHA("Primera Racha", "Completa tu primer día de retos", 50),
        RACHA_SEMANAL("Racha Semanal", "Mantén una racha de 7 días", 200),
        RACHA_MENSUAL("Racha Mensual", "Mantén una racha de 30 días", 500),
        CIEN_ESTRELLAS("100 Estrellas", "Obtén 100 estrellas totales", 300),
        RETO_DIFICIL("Maestro de Retos", "Completa 10 retos difíciles", 400),
        NIVEL_10("Nivel 10", "Alcanza el nivel 10", 1000),
        DEPORTISTA("Deportista", "Completa 20 retos de deportes", 250),
        ESTUDIANTE("Estudiante Dedicado", "Completa 20 retos de estudio", 250),
        ECOLOGISTA("Eco Guerrero", "Completa 20 retos de ecología", 250),
        SALUDABLE("Vida Saludable", "Completa 20 retos de salud", 250),
        CREATIVO("Mente Creativa", "Completa 20 retos de creatividad", 250);

        private final String title;
        private final String description;
        private final int points;

        AchievementType(String title, String description, int points) {
            this.title = title;
            this.description = description;
            this.points = points;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getPoints() { return points; }
    }

    // Constructor
    public Logro(int userId, String achievementType, String title, String description) {
        this.userId = userId;
        this.achievementType = achievementType;
        this.title = title;
        this.description = description;
        this.dateEarned = String.valueOf(System.currentTimeMillis()); // Usando timestamp compatible
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getAchievementType() { return achievementType; }
    public void setAchievementType(String achievementType) { this.achievementType = achievementType; }

    public String getDateEarned() { return dateEarned; }
    public void setDateEarned(String dateEarned) { this.dateEarned = dateEarned; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }
}
