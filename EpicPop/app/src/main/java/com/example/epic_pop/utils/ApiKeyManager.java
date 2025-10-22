package com.example.epic_pop.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class ApiKeyManager {

    private static final String PREFS_NAME = "epic_pop_secure_prefs";
    private static final String GROQ_API_KEY = "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";
    private static final String DEEZER_API_URL = "https://api.deezer.com/album/302127";

    private SharedPreferences securePrefs;

    public ApiKeyManager(Context context) {
        try {
            // Usando SharedPreferences regular para mayor compatibilidad
            securePrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        } catch (Exception e) {
            android.util.Log.e("EpicPop", "Error inicializando ApiKeyManager", e);
        }
    }

    public String getGroqApiKey() {
        return GROQ_API_KEY;
    }

    public String getDeezerApiUrl() {
        return DEEZER_API_URL;
    }

    public void saveUserToken(String token) {
        if (securePrefs != null) {
            securePrefs.edit().putString("user_token", token).apply();
        }
    }

    public String getUserToken() {
        if (securePrefs != null) {
            return securePrefs.getString("user_token", null);
        }
        return null;
    }

    public void clearUserToken() {
        if (securePrefs != null) {
            securePrefs.edit().remove("user_token").apply();
        }
    }
}
