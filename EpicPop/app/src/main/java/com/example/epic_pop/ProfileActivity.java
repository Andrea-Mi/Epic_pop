package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Usuario;
import com.example.epic_pop.utils.GamificationManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*; // Para List, Set, Calendar

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private static final int RC_EDIT_PROFILE = 201;
    private ImageView ivProfileAvatar;
    private TextView tvUsername, tvLevel, tvTotalStars, tvStreak, tvCompletedChallenges, tvBestStreak;
    private Button btnEditProfile, btnLogout, btnPlayMotivational, btnStopMusic;
    private EpicPopDatabase database;
    private GamificationManager gamificationManager;
    private ExecutorService executor;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "ProfileActivity iniciando...");

        try {
            setContentView(R.layout.activity_profile);

            database = EpicPopDatabase.getDatabase(this);
            gamificationManager = new GamificationManager(this);
            executor = Executors.newSingleThreadExecutor();

            getUserId();
            initViews();
            setupClickListeners();
            loadUserProfile();

        } catch (Exception e) {
            Log.e(TAG, "Error en ProfileActivity onCreate", e);
        }
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void initViews() {
        try {
            ivProfileAvatar = findViewById(R.id.iv_profile_avatar);
            tvUsername = findViewById(R.id.tv_profile_username);
            tvLevel = findViewById(R.id.tv_profile_level);
            tvTotalStars = findViewById(R.id.tv_profile_stars);
            tvStreak = findViewById(R.id.tv_profile_streak);
            tvCompletedChallenges = findViewById(R.id.tv_completed_challenges);
            tvBestStreak = findViewById(R.id.tv_profile_best_streak);
            btnEditProfile = findViewById(R.id.btn_edit_profile);
            btnLogout = findViewById(R.id.btn_logout);
            btnPlayMotivational = findViewById(R.id.btn_play_motivational);
            btnStopMusic = findViewById(R.id.btn_stop_music);

            findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas en ProfileActivity", e);
        }
    }

    private void setupClickListeners() {
        try {
            btnEditProfile.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivityForResult(intent, RC_EDIT_PROFILE);
            });

            btnLogout.setOnClickListener(v -> logout());

            btnPlayMotivational.setOnClickListener(v -> {
                Toast.makeText(this, "🎵 Reproduciendo música motivacional", Toast.LENGTH_SHORT).show();
            });

            btnStopMusic.setOnClickListener(v -> {
                Toast.makeText(this, "🎵 Música detenida", Toast.LENGTH_SHORT).show();
            });

            tvTotalStars.setOnClickListener(v -> Toast.makeText(this, getString(R.string.tooltip_yellow_stars), Toast.LENGTH_SHORT).show());
            tvStreak.setOnClickListener(v -> Toast.makeText(this, getString(R.string.tooltip_streak), Toast.LENGTH_SHORT).show());
        } catch (Exception e) {
            Log.e(TAG, "Error configurando click listeners", e);
        }
    }

    private void loadUserProfile() {
        executor.execute(() -> {
            Usuario usuario = null;
            int completedChallenges = 0;
            List<String> completedDatesRaw = new ArrayList<>();
            try {
                usuario = database.usuarioDao().getUsuarioById(userId);
                if (usuario != null) {
                    completedChallenges = database.retoDao().getCompletedRetosCount(userId);
                    completedDatesRaw = database.retoDao().getAllCompletedDates(userId);
                    // Verificar y otorgar logros
                    gamificationManager.checkAchievements(userId);
                    // Recalcular nivel basado en estrellas totales (no cambiamos lógica existente)
                    int newLevel = gamificationManager.calculateUserLevel(usuario.getTotalStars());
                    if (newLevel != usuario.getLevel()) {
                        database.usuarioDao().updateLevel(userId, newLevel);
                        usuario.setLevel(newLevel);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error cargando perfil de usuario", e);
            }

            final Usuario finalUsuario = usuario;
            final int finalCompletedChallenges = completedChallenges;
            final List<String> finalCompletedDatesRaw = completedDatesRaw;
            runOnUiThread(() -> {
                if (finalUsuario != null) {
                    updateUI(finalUsuario, finalCompletedChallenges, finalCompletedDatesRaw);
                } else {
                    Toast.makeText(ProfileActivity.this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateUI(Usuario usuario, int completedChallenges, List<String> completedDatesRaw) {
        try {
            SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
            String prefName = prefs.getString("username", usuario.getUsername());
            tvUsername.setText(prefName);
            tvLevel.setText("Nivel " + usuario.getLevel());
            // Calcular métricas dinámicas
            Stats stats = computeStats(completedDatesRaw);
            tvTotalStars.setText(getString(R.string.profile_total_yellow_stars, stats.yellowStars));
            tvStreak.setText(getString(R.string.profile_current_streak_days, stats.currentStreak));
            if (stats.bestStreak > 0) {
                tvBestStreak.setText(getString(R.string.profile_best_streak, stats.bestStreak));
            } else {
                tvBestStreak.setText("");
            }
            tvCompletedChallenges.setText(completedChallenges + " retos completados");
            ivProfileAvatar.setImageResource(R.drawable.default_avatar);
            // Actualizar racha en DB si difiere (solo cuenta ON_TIME consecutivos -> currentStreak)
            if (usuario.getStreak() != stats.currentStreak) {
                executor.execute(() -> {
                    try { database.usuarioDao().updateStreak(userId, stats.currentStreak); } catch (Exception ignored) {}
                });
            }
            // Best streak record en SharedPreferences
            int storedBest = prefs.getInt("best_streak_user_" + userId, 0);
            if (stats.bestStreak > storedBest) {
                prefs.edit().putInt("best_streak_user_" + userId, stats.bestStreak).apply();
                Toast.makeText(this, getString(R.string.streak_new_record), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error actualizando UI del perfil", e);
        }
    }

    private static class Stats {
        int yellowStars; // días con completions (ON_TIME)
        int currentStreak; // consecutivos hasta hoy
        int bestStreak; // máximo histórico
    }

    private Stats computeStats(List<String> completedDatesRaw) {
        Stats s = new Stats();
        if (completedDatesRaw == null || completedDatesRaw.isEmpty()) {
            return s;
        }
        Set<Long> dayStarts = new HashSet<>();
        Calendar cal = Calendar.getInstance();
        for (String tsStr : completedDatesRaw) {
            try {
                long ts = Long.parseLong(tsStr);
                cal.setTimeInMillis(ts);
                setToDayStart(cal);
                dayStarts.add(cal.getTimeInMillis());
            } catch (Exception ignored) {}
        }
        s.yellowStars = dayStarts.size();
        if (dayStarts.isEmpty()) return s;
        // current streak (solo días con completions consecutivos hasta hoy)
        Calendar today = Calendar.getInstance();
        setToDayStart(today);
        long oneDay = 24L*60*60*1000;
        long cursor = today.getTimeInMillis();
        while (dayStarts.contains(cursor)) {
            s.currentStreak++;
            cursor -= oneDay;
        }
        // best streak: evaluar todas las secuencias
        List<Long> sorted = new ArrayList<>(dayStarts);
        Collections.sort(sorted);
        int streak = 1;
        s.bestStreak = 1;
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i) - sorted.get(i-1) == oneDay) {
                streak++;
            } else {
                if (streak > s.bestStreak) s.bestStreak = streak;
                streak = 1;
            }
        }
        if (streak > s.bestStreak) s.bestStreak = streak;
        return s;
    }

    private void setToDayStart(Calendar c) {
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
    }

    private void logout() {
        try {
            SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
            prefs.edit().clear().apply();
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error durante logout", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_EDIT_PROFILE && resultCode == RESULT_OK) {
            loadUserProfile(); // refrescar datos, incluido nombre
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // En caso de que nombre haya cambiado sin resultado (navegación back)
        loadUserProfile();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
