package com.example.epic_pop.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.example.epic_pop.R;

public class ThemeUtils {
    public static final String PREFS = "epic_pop_prefs";
    public static final String KEY_THEME = "app_theme"; // values: purple, yellow, green

    public static void saveTheme(Context ctx, String theme) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY_THEME, theme).apply();
    }

    public static String getTheme(Context ctx) {
        return ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_THEME, "purple");
    }

    public static int gradientFor(String theme) {
        switch (theme) {
            case "yellow": return R.drawable.bg_theme_yellow;
            case "green": return R.drawable.bg_theme_green;
            default: return R.drawable.bg_theme_purple;
        }
    }

    public static int statusBarColorFor(String theme, Context ctx) {
        switch (theme) {
            case "yellow": return 0xFFFFD700; // #FFD700
            case "green": return 0xFF4CAF50; // #4CAF50
            default: return 0xFF673AB7; // #673AB7
        }
    }

    public static void applyToActivity(Activity act, View root) {
        String theme = getTheme(act);
        if (root != null) root.setBackgroundResource(gradientFor(theme));
        act.getWindow().setStatusBarColor(statusBarColorFor(theme, act));
    }
}
