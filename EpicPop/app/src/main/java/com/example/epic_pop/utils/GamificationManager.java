package com.example.epic_pop.utils;

import android.content.Context;
import android.util.Log;
import com.example.epic_pop.R; // import necesario para recursos de strings
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Logro;
import com.example.epic_pop.models.Usuario;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GamificationManager {

    private static final String TAG = "EpicPop";
    private EpicPopDatabase database;
    private Context context;
    private ExecutorService executor;

    public GamificationManager(Context context) {
        this.context = context;
        this.database = EpicPopDatabase.getDatabase(context);
        this.executor = Executors.newSingleThreadExecutor();
    }

    /**
     * Verifica y otorga logros según el progreso del usuario
     */
    public void checkAchievements(int userId) {
        executor.execute(() -> {
            try {
                Usuario usuario = database.usuarioDao().getUsuarioById(userId);
                if (usuario == null) return;

                Log.d(TAG, "Verificando logros para usuario: " + usuario.getUsername());

                // Verificar logro de primera racha
                if (usuario.getStreak() >= 1) {
                    checkAndAwardAchievement(userId, "PRIMERA_RACHA", "Primera Racha", "¡Completaste tu primer día de retos!");
                }

                // Verificar logro de racha semanal
                if (usuario.getStreak() >= 7) {
                    checkAndAwardAchievement(userId, "RACHA_SEMANAL", "Racha Semanal", "¡Mantuviste una racha de 7 días!");
                }

                // Verificar logro de racha mensual
                if (usuario.getStreak() >= 30) {
                    checkAndAwardAchievement(userId, "RACHA_MENSUAL", "Racha Mensual", "¡Increíble! 30 días consecutivos!");
                }

                // Verificar logro de 100 estrellas
                if (usuario.getTotalStars() >= 100) {
                    checkAndAwardAchievement(userId, "CIEN_ESTRELLAS", "100 Estrellas", "¡Obtuviste 100 estrellas totales!");
                }

                // Verificar logros por categoría
                checkCategoryAchievements(userId);

            } catch (Exception e) {
                Log.e(TAG, "Error verificando logros", e);
            }
        });
    }

    /**
     * Actualiza la racha del usuario
     */
    public void updateStreak(int userId) {
        executor.execute(() -> {
            try {
                Usuario usuario = database.usuarioDao().getUsuarioById(userId);
                if (usuario != null) {
                    // Verificar si completó retos hoy
                    int newStreak = usuario.getStreak() + 1;
                    database.usuarioDao().updateStreak(userId, newStreak);
                    Log.d(TAG, "Racha actualizada a: " + newStreak);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error actualizando racha", e);
            }
        });
    }

    /**
     * Calcula las estrellas ganadas según dificultad y puntualidad
     */
    public int calculateStarsEarned(String difficulty, boolean onTime) {
        int baseStars = 1;

        switch (difficulty) {
            case "Medio":
                baseStars = 2;
                break;
            case "Difícil":
                baseStars = 3;
                break;
        }

        // Bonificación por puntualidad
        if (onTime && baseStars < 3) {
            baseStars++;
        }

        return baseStars;
    }

    /**
     * Calcula el nivel del usuario basado en estrellas totales
     */
    public int calculateUserLevel(int totalStars) {
        // Cada 50 estrellas = 1 nivel
        return Math.max(1, (totalStars / 50) + 1);
    }

    private void checkAndAwardAchievement(int userId, String type, String title, String description) {
        try {
            // Verificar si ya tiene este logro
            int existingAchievements = database.logroDao().checkLogroExists(userId, type);
            if (existingAchievements == 0) {
                // Otorgar nuevo logro
                Logro logro = new Logro(userId, type, title, description);
                logro.setPointsAwarded(getPointsForAchievement(type));
                database.logroDao().insertLogro(logro);
                Log.d(TAG, "Nuevo logro otorgado: " + title);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error otorgando logro", e);
        }
    }

    private void checkCategoryAchievements(int userId) {
        try {
            // Verificar logros por categoría (20 retos completados)
            String[] categories = {"Deportes", "Estudio", "Ecología", "Salud", "Creatividad"};
            String[] achievementTypes = {"DEPORTISTA", "ESTUDIANTE", "ECOLOGISTA", "SALUDABLE", "CREATIVO"};
            String[] titles = {"Deportista", "Estudiante Dedicado", "Eco Guerrero", "Vida Saludable", "Mente Creativa"};

            for (int i = 0; i < categories.length; i++) {
                int completedRetos = database.retoDao().getCompletedRetosByCategory(userId, categories[i]);
                if (completedRetos >= 20) {
                    checkAndAwardAchievement(userId, achievementTypes[i], titles[i],
                        "¡Completaste 20 retos de " + categories[i].toLowerCase() + "!");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error verificando logros por categoría", e);
        }
    }

    public void awardChallengeCompletion(int userId, String category, int completedRetosCount, String challengeTitle) {
        executor.execute(() -> {
            try {
                String baseType = "CHALLENGE_COMPLETED";
                String dynamicMessage = buildDynamicMessage(category);
                String finalTitle = (challengeTitle != null && !challengeTitle.trim().isEmpty()) ? challengeTitle.trim() : getTitleForCategory(category);
                Logro logro = new Logro(userId, baseType, finalTitle, dynamicMessage);
                logro.setPointsAwarded(25);
                database.logroDao().insertLogro(logro);
                checkMilestones(userId, completedRetosCount);
            } catch (Exception e) {
                Log.e(TAG, "Error awarding challenge completion achievement", e);
            }
        });
    }

    private void checkMilestones(int userId, int completedRetosCount) {
        try {
            if (completedRetosCount == 5) {
                checkAndAwardAchievement(userId, "MILESTONE_5", "Nivel Bronce", context.getString(R.string.achievement_milestone_bronze));
            } else if (completedRetosCount == 10) {
                checkAndAwardAchievement(userId, "MILESTONE_10", "Nivel Plata", context.getString(R.string.achievement_milestone_silver));
            } else if (completedRetosCount == 20) {
                checkAndAwardAchievement(userId, "MILESTONE_20", "Nivel Oro", context.getString(R.string.achievement_milestone_gold));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking milestones", e);
        }
    }

    private String buildDynamicMessage(String category) {
        if (category == null) return context.getString(R.string.achievement_dynamic_message_fallback);
        switch (category) {
            case "Salud":
                return context.getString(R.string.achievement_category_health);
            case "Deportes":
                return context.getString(R.string.achievement_category_sports);
            case "Estudio":
                return context.getString(R.string.achievement_category_study);
            case "Ecología":
                return context.getString(R.string.achievement_category_ecology);
            case "Creatividad":
                return context.getString(R.string.achievement_category_creativity);
            default:
                return context.getString(R.string.achievement_dynamic_message_fallback);
        }
    }

    private String getTitleForCategory(String category) {
        if (category == null) return context.getString(R.string.achievement_new_challenge_completed);
        return context.getString(R.string.achievement_new_challenge_completed);
    }

    private int getPointsForAchievement(String type) {
        switch (type) {
            case "PRIMERA_RACHA": return 50;
            case "RACHA_SEMANAL": return 200;
            case "RACHA_MENSUAL": return 500;
            case "CIEN_ESTRELLAS": return 300;
            case "RETO_DIFICIL": return 400;
            case "NIVEL_10": return 1000;
            default: return 250;
        }
    }

    public void release() {
        if (executor != null) {
            executor.shutdown();
        }
    }
}
