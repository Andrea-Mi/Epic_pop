package com.example.epic_pop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private static final int SPLASH_DURATION = 2000; // 2 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "SplashActivity iniciando...");

        try {
            setContentView(R.layout.activity_splash);
            Log.d(TAG, "Layout del splash cargado exitosamente");

            // Navegar a LoginActivity después del splash usando Handler moderno
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    Log.d(TAG, "Navegando a LoginActivity...");
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e(TAG, "Error navegando a LoginActivity", e);
                }
            }, SPLASH_DURATION);

        } catch (Exception e) {
            Log.e(TAG, "Error en SplashActivity onCreate", e);
            // Si hay error, ir directamente al login sin delay
            try {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception e2) {
                Log.e(TAG, "Error crítico en fallback", e2);
            }
        }
    }
}
