package com.example.epic_pop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Reto;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChallengeListActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private RecyclerView rvChallenges;
    private TextView tvTotalChallenges, tvCompletedChallenges, tvPendingChallenges;
    private TabLayout tabLayout;
    private EpicPopDatabase database;
    private ChallengeListAdapter challengeAdapter;
    private ExecutorService executor;
    private int userId;
    private List<Reto> allRetos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "ChallengeListActivity iniciando...");

        try {
            setContentView(R.layout.activity_challenge_list);

            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();
            getUserId();

            initViews();
            setupRecyclerView();
            setupTabs();
            setupClickListeners();
            loadAllChallenges();

        } catch (Exception e) {
            Log.e(TAG, "Error en ChallengeListActivity onCreate", e);
        }
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void initViews() {
        try {
            rvChallenges = findViewById(R.id.rv_challenges);
            tvTotalChallenges = findViewById(R.id.tv_total_challenges);
            tvCompletedChallenges = findViewById(R.id.tv_completed_challenges);
            tvPendingChallenges = findViewById(R.id.tv_pending_challenges);
            tabLayout = findViewById(R.id.tab_layout);

            findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas", e);
        }
    }

    private void setupRecyclerView() {
        try {
            challengeAdapter = new ChallengeListAdapter(new ArrayList<>(), this);
            rvChallenges.setLayoutManager(new LinearLayoutManager(this));
            rvChallenges.setAdapter(challengeAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error configurando RecyclerView", e);
        }
    }

    private void setupTabs() {
        try {
            tabLayout.addTab(tabLayout.newTab().setText("Todos"));
            tabLayout.addTab(tabLayout.newTab().setText("Pendientes"));
            tabLayout.addTab(tabLayout.newTab().setText("En Progreso"));
            tabLayout.addTab(tabLayout.newTab().setText("Completados"));

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    filterChallenges(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}

                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "Error configurando tabs", e);
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.btn_refresh).setOnClickListener(v -> {
            Log.d(TAG, "Botón refresh presionado - Sincronizando...");
            loadAllChallenges();
        });
    }

    private void loadAllChallenges() {
        executor.execute(() -> {
            List<Reto> retos = new ArrayList<>();
            int totalCount = 0;
            int completedCount = 0;
            int pendingCount = 0;

            try {
                // Cargar todos los retos del usuario
                retos = database.retoDao().getRetosByUserId(userId);
                totalCount = retos.size();

                // Calcular estadísticas
                for (Reto reto : retos) {
                    if ("Completado".equals(reto.getStatus())) {
                        completedCount++;
                    } else {
                        pendingCount++;
                    }
                }

                Log.d(TAG, "Retos cargados: " + totalCount + " total, " + completedCount + " completados, " + pendingCount + " pendientes");

            } catch (Exception e) {
                Log.e(TAG, "Error cargando lista de retos", e);
            }

            // Volver al hilo principal
            final List<Reto> finalRetos = retos;
            final int finalTotal = totalCount;
            final int finalCompleted = completedCount;
            final int finalPending = pendingCount;

            runOnUiThread(() -> {
                try {
                    allRetos = finalRetos;
                    challengeAdapter.updateChallenges(finalRetos);

                    // Actualizar estadísticas
                    tvTotalChallenges.setText("Total: " + finalTotal);
                    tvCompletedChallenges.setText("Completados: " + finalCompleted);
                    tvPendingChallenges.setText("Pendientes: " + finalPending);

                } catch (Exception e) {
                    Log.e(TAG, "Error actualizando UI", e);
                }
            });
        });
    }

    private void filterChallenges(int tabPosition) {
        List<Reto> filteredRetos = new ArrayList<>();

        switch (tabPosition) {
            case 0: // Todos
                filteredRetos = new ArrayList<>(allRetos);
                break;
            case 1: // Pendientes
                for (Reto reto : allRetos) {
                    if ("Pendiente".equals(reto.getStatus())) {
                        filteredRetos.add(reto);
                    }
                }
                break;
            case 2: // En Progreso
                for (Reto reto : allRetos) {
                    if ("En Progreso".equals(reto.getStatus())) {
                        filteredRetos.add(reto);
                    }
                }
                break;
            case 3: // Completados
                for (Reto reto : allRetos) {
                    if ("Completado".equals(reto.getStatus())) {
                        filteredRetos.add(reto);
                    }
                }
                break;
        }

        challengeAdapter.updateChallenges(filteredRetos);
        Log.d(TAG, "Filtro aplicado - Tab: " + tabPosition + ", Retos mostrados: " + filteredRetos.size());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cada vez que volvemos a esta pantalla para mantener sincronización
        Log.d(TAG, "ChallengeListActivity onResume - Sincronizando lista...");
        loadAllChallenges();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) {
            executor.shutdown();
        }
    }
}
