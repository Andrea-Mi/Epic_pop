package com.example.epic_pop.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.epic_pop.models.Reto;
import java.util.List;

@Dao
public interface RetoDao {

    @Insert
    long insertReto(Reto reto);

    @Update
    void updateReto(Reto reto);

    @Delete
    void deleteReto(Reto reto);

    @Query("SELECT * FROM retos WHERE id = :id")
    Reto getRetoById(int id);

    @Query("SELECT * FROM retos WHERE userId = :userId ORDER BY createdDate DESC")
    List<Reto> getRetosByUserId(int userId);

    @Query("SELECT * FROM retos WHERE userId = :userId AND status = :status")
    List<Reto> getRetosByUserIdAndStatus(int userId, String status);

    @Query("SELECT * FROM retos WHERE userId = :userId AND category = :category")
    List<Reto> getRetosByUserIdAndCategory(int userId, String category);

    @Query("UPDATE retos SET progress = :progress WHERE id = :retoId")
    void updateProgress(int retoId, int progress);

    @Query("UPDATE retos SET status = :status WHERE id = :retoId")
    void updateStatus(int retoId, String status);

    @Query("UPDATE retos SET status = :status, completedDate = :completedDate, starsAwarded = :stars WHERE id = :retoId")
    void completeReto(int retoId, String status, String completedDate, int stars);

    @Query("SELECT COUNT(*) FROM retos WHERE userId = :userId AND status = 'Completado'")
    int getCompletedRetosCount(int userId);

    @Query("SELECT COUNT(*) FROM retos WHERE userId = :userId AND category = :category AND status = 'Completado'")
    int getCompletedRetosByCategory(int userId, String category);

    @Query("SELECT AVG(progress) FROM retos WHERE userId = :userId")
    float getAverageProgress(int userId);

    @Query("SELECT SUM(starsAwarded) FROM retos WHERE userId = :userId")
    int getTotalStarsEarned(int userId);

    @Query("SELECT * FROM retos WHERE userId = :userId AND difficulty = :difficulty AND status = 'Completado'")
    List<Reto> getCompletedRetosByDifficulty(int userId, String difficulty);

    @Query("SELECT CASE WHEN COUNT(*)=0 THEN 0 ELSE (SUM(CASE status WHEN 'Completado' THEN 100 WHEN 'En Progreso' THEN 50 ELSE 0 END)*1.0)/COUNT(*) END FROM retos WHERE userId = :userId")
    float getDynamicGlobalProgress(int userId);

    @Query("UPDATE retos SET progress = CASE status WHEN 'Completado' THEN 100 WHEN 'En Progreso' THEN 50 ELSE 0 END WHERE userId = :userId")
    void recalcProgressByStatus(int userId);

    @Query("SELECT completedDate FROM retos WHERE userId = :userId AND completedDate IS NOT NULL AND CAST(completedDate AS INTEGER) BETWEEN :startMillis AND :endMillis")
    List<String> getCompletedDatesInRange(int userId, long startMillis, long endMillis);

    // Nueva consulta: todas las fechas completadas (timestamps) para cálculo de estrellas amarillas
    @Query("SELECT completedDate FROM retos WHERE userId = :userId AND completedDate IS NOT NULL")
    List<String> getAllCompletedDates(int userId);
}
