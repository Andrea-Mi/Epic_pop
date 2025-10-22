package com.example.epic_pop.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationUtils {
    public static final String PREFS = "epic_pop_prefs";
    public static final String KEY_ENABLED = "notifications_enabled";
    public static final String KEY_CHALLENGE_REMINDERS = "challenge_reminders_enabled";
    public static final String KEY_ACHIEVEMENTS = "achievements_enabled";
    public static final String KEY_DAILY_MOTIVATION = "daily_motivation_enabled";

    public static boolean isEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ENABLED, true);
    }

    public static void setEnabled(Context ctx, boolean enabled) {
        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_ENABLED, enabled).apply();
        if (!enabled) cancelAll(ctx);
    }

    // Métodos para recordatorios de retos
    public static boolean isChallengeRemindersEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_CHALLENGE_REMINDERS, true);
    }

    public static void setChallengeRemindersEnabled(Context ctx, boolean enabled) {
        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_CHALLENGE_REMINDERS, enabled).apply();
    }

    // Métodos para notificaciones de logros
    public static boolean isAchievementsEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_ACHIEVEMENTS, true);
    }

    public static void setAchievementsEnabled(Context ctx, boolean enabled) {
        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_ACHIEVEMENTS, enabled).apply();
    }

    // Métodos para motivación diaria
    public static boolean isDailyMotivationEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_DAILY_MOTIVATION, true);
    }

    public static void setDailyMotivationEnabled(Context ctx, boolean enabled) {
        SharedPreferences.Editor ed = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit();
        ed.putBoolean(KEY_DAILY_MOTIVATION, enabled).apply();
    }

    // Método helper para verificar si un tipo específico de notificación debe enviarse
    public static boolean shouldSendNotification(Context ctx, String type) {
        if (!isEnabled(ctx)) return false;

        switch (type) {
            case "challenge_reminder":
                return isChallengeRemindersEnabled(ctx);
            case "achievement":
                return isAchievementsEnabled(ctx);
            case "daily_motivation":
                return isDailyMotivationEnabled(ctx);
            default:
                return true;
        }
    }

    public static void cancelAll(Context ctx) {
        try {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.cancelAll();
        } catch (Exception ignored) {}
        // Nota: si usas AlarmManager/WorkManager para notificaciones, aquí también cancela o pausa trabajos programados.
    }
}
