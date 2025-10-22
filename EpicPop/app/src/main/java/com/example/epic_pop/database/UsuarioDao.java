package com.example.epic_pop.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.epic_pop.models.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {

    @Insert
    long insertUsuario(Usuario usuario);

    @Update
    void updateUsuario(Usuario usuario);

    @Delete
    void deleteUsuario(Usuario usuario);

    @Query("SELECT * FROM usuarios WHERE id = :id")
    Usuario getUsuarioById(int id);

    @Query("SELECT * FROM usuarios WHERE email = :email")
    Usuario getUsuarioByEmail(String email);

    @Query("SELECT * FROM usuarios WHERE email = :email AND password = :password")
    Usuario login(String email, String password);

    @Query("SELECT * FROM usuarios")
    List<Usuario> getAllUsuarios();

    @Query("UPDATE usuarios SET streak = :streak WHERE id = :userId")
    void updateStreak(int userId, int streak);

    @Query("UPDATE usuarios SET totalStars = :totalStars WHERE id = :userId")
    void updateTotalStars(int userId, int totalStars);

    @Query("UPDATE usuarios SET level = :level WHERE id = :userId")
    void updateLevel(int userId, int level);

    @Query("UPDATE usuarios SET lastLogin = :lastLogin WHERE id = :userId")
    void updateLastLogin(int userId, String lastLogin);

    @Query("UPDATE usuarios SET avatarUrl = :avatarUrl WHERE id = :userId")
    void updateAvatarUrl(int userId, String avatarUrl);

    @Query("SELECT COUNT(*) FROM usuarios WHERE email = :email")
    int checkEmailExists(String email);

    @Query("UPDATE usuarios SET username = :username WHERE id = :userId")
    void updateUsername(int userId, String username);
}
