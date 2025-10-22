package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Logro;
import com.example.epic_pop.models.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AchievementsActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private RecyclerView rvAchievements;
    private TextView tvTotalAchievements;
    private TextView tvStreak;
    private TextView tvTotalRetos;
    private TextView tvEmptyHint;
    private ImageButton btnShare;
    private EpicPopDatabase database;
    private AchievementsAdapter achievementsAdapter;
    private ExecutorService executor;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AchievementsActivity iniciando...");
        try {
            setContentView(R.layout.activity_achievements);
            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();
            getUserId();
            initViews();
            setupRecyclerView();
            setupShareButton();
            loadAchievements();
        } catch (Exception e) {
            Log.e(TAG, "Error en AchievementsActivity onCreate", e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAchievements();
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
    }

    private void initViews() {
        try {
            rvAchievements = findViewById(R.id.rv_achievements);
            tvTotalAchievements = findViewById(R.id.tv_total_achievements);
            tvStreak = findViewById(R.id.tv_streak);
            tvTotalRetos = findViewById(R.id.tv_total_retos);
            tvEmptyHint = findViewById(R.id.tv_empty_hint);
            btnShare = findViewById(R.id.btn_share);
            View btnBack = findViewById(R.id.btn_back);
            if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas", e);
        }
    }

    private void setupRecyclerView() {
        try {
            achievementsAdapter = new AchievementsAdapter(new ArrayList<>());
            rvAchievements.setLayoutManager(new LinearLayoutManager(this));
            rvAchievements.setAdapter(achievementsAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error configurando RecyclerView", e);
        }
    }

    private void setupShareButton() {
        if (btnShare != null) {
            btnShare.setOnClickListener(v -> {
                try {
                    int totalAch = (achievementsAdapter != null) ? achievementsAdapter.getItemCount() : 0;
                    int completedRetos = database.retoDao().getCompletedRetosCount(userId);
                    String shareText = getString(R.string.achievements_share_text, totalAch, completedRetos);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.achievements_share)));
                } catch (Exception e) {
                    Log.e(TAG, "Error compartiendo logros", e);
                }
            });
        }
    }

    private void loadAchievements() {
        executor.execute(() -> {
            List<Logro> logros = new ArrayList<>();
            int streak = 0;
            int completedRetos = 0;
            try {
                logros = database.logroDao().getLogrosByUserId(userId);
                Usuario u = database.usuarioDao().getUsuarioById(userId);
                if (u != null) streak = u.getStreak();
                completedRetos = database.retoDao().getCompletedRetosCount(userId);
                Log.d(TAG, "Logros cargados: " + logros.size());
            } catch (Exception e) {
                Log.e(TAG, "Error cargando logros", e);
            }
            final List<Logro> finalLogros = logros;
            final int finalStreak = streak;
            final int finalCompleted = completedRetos;
            runOnUiThread(() -> updateUI(finalLogros, finalStreak, finalCompleted));
        });
    }

    private void updateUI(List<Logro> logros, int streak, int completedRetos) {
        try {
            achievementsAdapter.updateAchievements(logros);
            tvTotalAchievements.setText(getString(R.string.achievements_title) + ": " + logros.size());
            if (streak > 0) {
                tvStreak.setText(getString(R.string.achievements_streak, streak));
                tvStreak.setVisibility(View.VISIBLE);
            } else {
                tvStreak.setVisibility(View.GONE);
            }
            if (completedRetos > 0) {
                tvTotalRetos.setText(getString(R.string.achievements_total_retos, completedRetos));
                tvTotalRetos.setVisibility(View.VISIBLE);
            } else {
                tvTotalRetos.setVisibility(View.GONE);
            }
            if (logros.isEmpty()) {
                tvEmptyHint.setVisibility(View.VISIBLE);
            } else {
                tvEmptyHint.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error actualizando UI de logros", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }
}
