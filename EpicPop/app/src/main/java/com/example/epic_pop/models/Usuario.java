package com.example.epic_pop.models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class Usuario {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String username;
    private String email;
    private String password;
    private String salt; // Agregando campo para almacenar el salt
    private String avatarUrl;
    private int level;
    private int streak;
    private int totalStars;
    private String createdDate;
    private String lastLogin;

    // Constructor
    public Usuario(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.level = 1;
        this.streak = 0;
        this.totalStars = 0;
        this.createdDate = String.valueOf(System.currentTimeMillis()); // Usando timestamp compatible
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getSalt() { return salt; }
    public void setSalt(String salt) { this.salt = salt; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public int getTotalStars() { return totalStars; }
    public void setTotalStars(int totalStars) { this.totalStars = totalStars; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastLogin() { return lastLogin; }
    public void setLastLogin(String lastLogin) { this.lastLogin = lastLogin; }
}
