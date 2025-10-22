package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Reto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private TextView tvGreeting, tvProgressPercentage;
    private ImageView ivAvatar;
    private RecyclerView rvRetos;
    private FloatingActionButton fabAddReto;
    private BottomNavigationView bottomNavigation;
    private LottieAnimationView lottieCharacter;
    private EpicPopDatabase database;
    private RetosAdapter retosAdapter;
    private ExecutorService executor;
    private int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "MainActivity iniciando...");

        try {
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout principal cargado exitosamente");

            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();
            getUserData();

            initViews();
            setupBackground();
            setupRecyclerView();
            setupClickListeners();
            loadUserData();

        } catch (Exception e) {
            Log.e(TAG, "Error en MainActivity onCreate", e);
        }
    }

    private void getUserData() {
        try {
            SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
            userId = prefs.getInt("user_id", -1);
            username = prefs.getString("username", "Usuario");

            // Verificar si el usuario está logueado
            if (userId == -1 || !prefs.getBoolean("is_logged_in", false)) {
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            Log.d(TAG, "Usuario logueado: " + username + " (ID: " + userId + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error obteniendo datos de usuario", e);
        }
    }

    private void initViews() {
        try {
            tvGreeting = findViewById(R.id.tv_greeting);
            tvProgressPercentage = findViewById(R.id.tv_progress_percentage);
            ivAvatar = findViewById(R.id.iv_avatar);
            rvRetos = findViewById(R.id.rv_retos);
            fabAddReto = findViewById(R.id.fab_add_reto);
            bottomNavigation = findViewById(R.id.bottom_navigation);
            lottieCharacter = findViewById(R.id.lottie_character);

            // Configurar saludo personalizado
            tvGreeting.setText(getString(R.string.main_greeting, username));

            Log.d(TAG, "Vistas del MainActivity inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas en MainActivity", e);
        }
    }

    private void setupBackground() {
        try {
            View mainLayout = findViewById(R.id.main_layout);
            if (mainLayout != null) {
                GradientDrawable gradient = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[]{Color.parseColor("#6200EE"), Color.parseColor("#BBDEFB")}
                );
                mainLayout.setBackground(gradient);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando fondo degradado", e);
        }
    }

    private void setupRecyclerView() {
        try {
            retosAdapter = new RetosAdapter(new ArrayList<>(), this);
            rvRetos.setLayoutManager(new LinearLayoutManager(this));
            rvRetos.setAdapter(retosAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error configurando RecyclerView", e);
        }
    }

    private void setupClickListeners() {
        try {
            fabAddReto.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddChallengeActivity.class);
                startActivityForResult(intent, 100); // Usar startActivityForResult para recibir respuesta
            });

            bottomNavigation.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    return true;
                } else if (itemId == R.id.nav_list) {
                    Intent intent = new Intent(MainActivity.this, ChallengeListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_achievements) {
                    Intent intent = new Intent(MainActivity.this, AchievementsActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_calendar) {
                    Intent intent = new Intent(MainActivity.this, CalendarActivity.class);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.nav_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            });

            ivAvatar.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            });

            if (lottieCharacter != null) {
                lottieCharacter.setOnClickListener(v -> {
                    Log.d(TAG, "Abriendo AI Assistant...");
                    Intent intent = new Intent(MainActivity.this, AIAssistantActivity.class);
                    startActivity(intent);
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Error configurando click listeners", e);
        }
    }

    private void loadUserData() {
        executor.execute(() -> {
            List<Reto> userRetos = new ArrayList<>();
            float dynamicProgress = 0f;
            try {
                userRetos = database.retoDao().getRetosByUserId(userId);
                if (!userRetos.isEmpty()) {
                    dynamicProgress = database.retoDao().getDynamicGlobalProgress(userId); // 0..100
                } else {
                    dynamicProgress = 0f;
                }
                Log.d(TAG, "Datos cargados: " + userRetos.size() + " retos, progreso dinámico: " + dynamicProgress + "%");
            } catch (Exception e) {
                Log.e(TAG, "Error cargando datos de usuario", e);
                userRetos = new ArrayList<>();
                dynamicProgress = 0f;
            }
            final List<Reto> finalRetos = userRetos;
            final float finalProgress = dynamicProgress;
            runOnUiThread(() -> {
                try {
                    // Sincronizar progress de cada reto con su status para la UI
                    for (Reto r : finalRetos) {
                        String st = r.getStatus();
                        int mapped;
                        if (st == null) {
                            mapped = 0;
                        } else if (st.equalsIgnoreCase(getString(R.string.status_completed)) || st.equalsIgnoreCase("Completado")) {
                            mapped = 100;
                        } else if (st.equalsIgnoreCase(getString(R.string.status_in_progress)) || st.equalsIgnoreCase("En Progreso")) {
                            mapped = 50;
                        } else {
                            mapped = 0;
                        }
                        if (r.getProgress() != mapped) {
                            r.setProgress(mapped); // Solo para UI; no escribimos a DB aquí para evitar I/O en main thread
                        }
                    }
                    retosAdapter.updateRetos(finalRetos);
                    if (finalRetos.isEmpty()) {
                        tvProgressPercentage.setText("0%");
                    } else {
                        java.util.Locale locale = java.util.Locale.getDefault();
                        String formatted = String.format(locale, "%.1f%%", finalProgress);
                        tvProgressPercentage.setText(formatted);
                    }
                    if (lottieCharacter != null) {
                        lottieCharacter.playAnimation();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error actualizando UI", e);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // Refrescar nombre de usuario por si fue editado en perfil
            SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
            String newUsername = prefs.getString("username", username);
            if (newUsername != null && !newUsername.equals(username)) {
                username = newUsername;
                if (tvGreeting != null) {
                    tvGreeting.setText(getString(R.string.main_greeting, username));
                }
            }
            Log.d(TAG, "MainActivity onResume - Recargando datos...");
            loadUserData(); // Recargar datos cada vez que volvemos a esta pantalla
            bottomNavigation.setSelectedItemId(R.id.nav_home);
        } catch (Exception e) {
            Log.e(TAG, "Error en onResume", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Un nuevo reto fue creado exitosamente
            boolean nuevoRetoCreado = data.getBooleanExtra("nuevo_reto_creado", false);
            String retoTitle = data.getStringExtra("reto_title");

            if (nuevoRetoCreado) {
                Log.d(TAG, "Nuevo reto detectado: " + retoTitle + " - Sincronizando lista...");
                Toast.makeText(this, "✅ Reto '" + retoTitle + "' agregado a tu lista", Toast.LENGTH_SHORT).show();

                // Recargar inmediatamente la lista de retos
                loadUserData();
            }
        } else {
            // Recargar datos cuando volvemos de cualquier otra actividad
            Log.d(TAG, "Regresando de actividad - Sincronizando lista de retos...");
            loadUserData();
        }
    }

    /**
     * Método público para forzar la recarga de datos desde otras actividades
     */
    public void refreshRetosData() {
        Log.d(TAG, "Forzando actualización de lista de retos...");
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}