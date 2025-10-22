package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Reto;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddChallengeActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private EditText etTitle, etDescription;
    private Spinner spinnerCategory, spinnerDifficulty;
    private Button btnSaveReto;
    private TextInputLayout tilTitle, tilDescription;
    private EpicPopDatabase database;
    private ExecutorService executor;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "AddChallengeActivity iniciando...");

        try {
            setContentView(R.layout.activity_add_challenge);

            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();
            getUserId();

            initViews();
            setupSpinners();
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error en AddChallengeActivity onCreate", e);
        }
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void initViews() {
        try {
            etTitle = findViewById(R.id.et_reto_title);
            etDescription = findViewById(R.id.et_reto_description);
            spinnerCategory = findViewById(R.id.spinner_category);
            spinnerDifficulty = findViewById(R.id.spinner_difficulty);
            btnSaveReto = findViewById(R.id.btn_save_reto);
            tilTitle = findViewById(R.id.til_reto_title);
            tilDescription = findViewById(R.id.til_reto_description);
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas en AddChallengeActivity", e);
        }
    }

    private void setupSpinners() {
        try {
            // Configurar spinner de categorías
            List<String> categories = Arrays.asList("Deportes", "Estudio", "Ecología", "Salud", "Creatividad");
            ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categories);
            spinnerCategory.setAdapter(categoryAdapter);

            // Configurar spinner de dificultad
            List<String> difficulties = Arrays.asList("Fácil", "Medio", "Difícil");
            ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, difficulties);
            spinnerDifficulty.setAdapter(difficultyAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error configurando spinners", e);
        }
    }

    private void setupClickListeners() {
        try {
            btnSaveReto.setOnClickListener(v -> attemptSaveReto());
            findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error configurando click listeners", e);
        }
    }

    private void attemptSaveReto() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        // Limpiar errores previos
        tilTitle.setError(null);
        tilDescription.setError(null);

        // Validaciones
        if (TextUtils.isEmpty(title)) {
            tilTitle.setError("El título es requerido");
            etTitle.requestFocus();
            return;
        }

        if (title.length() < 3) {
            tilTitle.setError("El título debe tener al menos 3 caracteres");
            etTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            tilDescription.setError("La descripción es requerida");
            etDescription.requestFocus();
            return;
        }

        // Guardar reto usando ExecutorService
        saveReto(title, description, category, difficulty);
    }

    private void saveReto(String title, String description, String category, String difficulty) {
        btnSaveReto.setEnabled(false);
        btnSaveReto.setText("Guardando...");

        executor.execute(() -> {
            boolean success = false;
            try {
                Reto reto = new Reto(userId, title, description, difficulty, category);
                long result = database.retoDao().insertReto(reto);
                success = result > 0;

                Log.d(TAG, "Reto guardado con ID: " + result);

                // Si se guardó exitosamente, notificar al MainActivity para sincronizar
                if (success) {
                    // Preparar datos para enviar de vuelta al MainActivity
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("nuevo_reto_creado", true);
                    resultIntent.putExtra("reto_title", title);
                    setResult(RESULT_OK, resultIntent);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error guardando reto", e);
            }

            // Volver al hilo principal
            final boolean finalSuccess = success;
            runOnUiThread(() -> {
                btnSaveReto.setEnabled(true);
                btnSaveReto.setText("Guardar Reto");

                if (finalSuccess) {
                    Toast.makeText(AddChallengeActivity.this, "¡Reto creado exitosamente!", Toast.LENGTH_SHORT).show();
                    finish(); // Esto activará onActivityResult en MainActivity
                } else {
                    Toast.makeText(AddChallengeActivity.this, "Error al crear el reto", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
