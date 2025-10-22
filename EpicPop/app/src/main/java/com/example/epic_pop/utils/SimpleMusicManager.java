package com.example.epic_pop.utils;

import android.content.Context;
import android.util.Log;

/**
 * Versión simplificada de MusicManager para evitar conflictos de dependencias
 * TODO: Implementar integración completa de Deezer en versión futura
 */
public class SimpleMusicManager {

    private static final String TAG = "EpicPop";
    private Context context;

    public SimpleMusicManager(Context context) {
        this.context = context;
    }

    /**
     * Reproduce música motivacional (placeholder)
     */
    public void playMotivationalMusic() {
        Log.d(TAG, "Reproduciendo música motivacional (simulado)");
        // TODO: Implementar integración real con Deezer API
    }

    /**
     * Para música (placeholder)
     */
    public void stop() {
        Log.d(TAG, "Deteniendo música (simulado)");
        // TODO: Implementar control real de reproducción
    }

    /**
     * Reproduce música por categoría (placeholder)
     */
    public void playMusicForCategory(String category) {
        Log.d(TAG, "Reproduciendo música para categoría: " + category + " (simulado)");
        // TODO: Implementar música temática por categoría
    }

    public boolean isPlaying() {
        return false; // Placeholder
    }

    public void release() {
        Log.d(TAG, "MusicManager liberado");
        // TODO: Implementar liberación de recursos
    }
}
