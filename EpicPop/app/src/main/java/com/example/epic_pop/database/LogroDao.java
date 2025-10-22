package com.example.epic_pop.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.epic_pop.models.Logro;
import java.util.List;

@Dao
public interface LogroDao {

    @Insert
    long insertLogro(Logro logro);

    @Update
    void updateLogro(Logro logro);

    @Delete
    void deleteLogro(Logro logro);

    @Query("SELECT * FROM logros WHERE id = :id")
    Logro getLogroById(int id);

    @Query("SELECT * FROM logros WHERE userId = :userId ORDER BY dateEarned DESC")
    List<Logro> getLogrosByUserId(int userId);

    @Query("SELECT * FROM logros WHERE userId = :userId AND achievementType = :type")
    List<Logro> getLogrosByUserIdAndType(int userId, String type);

    @Query("SELECT COUNT(*) FROM logros WHERE userId = :userId")
    int getLogrosCount(int userId);

    @Query("SELECT * FROM logros WHERE userId = :userId ORDER BY dateEarned DESC LIMIT 5")
    List<Logro> getRecentLogros(int userId);

    @Query("SELECT COUNT(*) FROM logros WHERE userId = :userId AND achievementType = :type")
    int checkLogroExists(int userId, String type);

    @Query("SELECT SUM(pointsAwarded) FROM logros WHERE userId = :userId")
    int getTotalPointsFromLogros(int userId);
}
