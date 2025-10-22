package com.example.epic_pop;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Reto;
import com.example.epic_pop.utils.GamificationManager;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditChallengeActivity extends AppCompatActivity {

    private EpicPopDatabase database;
    private int retoId = -1;
    private Reto reto;

    private TextInputLayout tilTitle, tilDeadline;
    private TextInputEditText etTitle, etDeadline;
    private MaterialAutoCompleteTextView autoFrequency, autoCategory, autoStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_challenge);

        database = EpicPopDatabase.getDatabase(this);
        retoId = getIntent().getIntExtra("reto_id", -1);

        bindViews();
        setupDropdowns();
        loadReto();
    }

    private void bindViews() {
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        tilTitle = findViewById(R.id.til_title);
        tilDeadline = findViewById(R.id.til_deadline);
        etTitle = findViewById(R.id.et_title);
        etDeadline = findViewById(R.id.et_deadline);
        autoFrequency = findViewById(R.id.auto_frequency);
        autoCategory = findViewById(R.id.auto_category);
        autoStatus = findViewById(R.id.auto_status);

        // End icon abre selector de fecha/hora
        if (tilDeadline != null) {
            tilDeadline.setEndIconOnClickListener(v -> showDateTimePicker());
        }

        // Guardar
        findViewById(R.id.btn_save).setOnClickListener(v -> saveReto());
        // Cancelar
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
        // Eliminar
        View btnDelete = findViewById(R.id.btn_delete);
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> {
                if (reto == null || reto.getId() == 0) {
                    Toast.makeText(this, "Reto no disponible para eliminar", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(this)
                        .setTitle(R.string.delete_challenge)
                        .setMessage("¿Seguro que deseas eliminar este reto? Esta acción no se puede deshacer.")
                        .setNegativeButton(R.string.action_cancel, (d, w) -> d.dismiss())
                        .setPositiveButton(R.string.delete_challenge, (d, w) -> deleteReto())
                        .show();
            });
        }
    }

    private void setupDropdowns() {
        if (autoFrequency != null) {
            String[] freqs = new String[]{"Diario", "Semanal", "Mensual"};
            autoFrequency.setSimpleItems(freqs);
        }
        if (autoCategory != null) {
            String[] cats = new String[]{
                    getString(R.string.category_health),
                    getString(R.string.category_sports),
                    getString(R.string.category_study),
                    getString(R.string.category_ecology),
                    getString(R.string.category_creativity)
            };
            autoCategory.setSimpleItems(cats);
        }
        if (autoStatus != null) {
            String[] sts = new String[]{
                    getString(R.string.status_pending),
                    getString(R.string.status_in_progress),
                    getString(R.string.status_completed)
            };
            autoStatus.setSimpleItems(sts);
        }
        if (etDeadline != null) etDeadline.setOnClickListener(v -> showDateTimePicker());
    }

    private void loadReto() {
        if (retoId == -1) return;
        new Thread(() -> {
            reto = database.retoDao().getRetoById(retoId);
            runOnUiThread(() -> populateUI());
        }).start();
    }

    private void populateUI() {
        if (reto == null) return;
        if (etTitle != null) etTitle.setText(reto.getTitle());
        if (autoCategory != null && !TextUtils.isEmpty(reto.getCategory())) autoCategory.setText(reto.getCategory(), false);
        if (autoStatus != null && !TextUtils.isEmpty(reto.getStatus())) autoStatus.setText(reto.getStatus(), false);
        if (!TextUtils.isEmpty(reto.getDueDate())) {
            try {
                long ts = Long.parseLong(reto.getDueDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("es"));
                if (etDeadline != null) etDeadline.setText(sdf.format(ts));
                if (etDeadline != null) etDeadline.setTag(ts);
            } catch (Exception ignored) {}
        }
    }

    private void saveReto() {
        if (reto == null) return;
        String originalStatus = reto.getStatus();
        String title = etTitle != null && etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
        if (title.length() < 3) {
            if (tilTitle != null) tilTitle.setError(getString(R.string.validation_title_min));
            if (etTitle != null) etTitle.requestFocus();
            return;
        } else if (tilTitle != null) {
            tilTitle.setError(null);
        }

        reto.setTitle(title);
        if (autoCategory != null) reto.setCategory(autoCategory.getText() != null ? autoCategory.getText().toString() : reto.getCategory());
        if (autoStatus != null) {
            String newStatus = autoStatus.getText() != null ? autoStatus.getText().toString() : reto.getStatus();
            reto.setStatus(newStatus);
            int mappedProgress;
            if (newStatus.equalsIgnoreCase(getString(R.string.status_completed)) || newStatus.equalsIgnoreCase("Completado")) {
                mappedProgress = 100;
                if (!"Completado".equalsIgnoreCase(originalStatus)) {
                    reto.setCompletedDate(String.valueOf(System.currentTimeMillis()));
                }
            } else if (newStatus.equalsIgnoreCase(getString(R.string.status_in_progress)) || newStatus.equalsIgnoreCase("En Progreso")) {
                mappedProgress = 50;
            } else {
                mappedProgress = 0;
            }
            reto.setProgress(mappedProgress);
        }
        Object ts = etDeadline != null ? etDeadline.getTag() : null;
        if (ts instanceof Long) reto.setDueDate(String.valueOf((Long) ts));

        new Thread(() -> {
            try {
                database.retoDao().updateReto(reto);
                database.retoDao().updateProgress(reto.getId(), reto.getProgress());
                // Otorgar logro si transición a completado
                if (!"Completado".equalsIgnoreCase(originalStatus) && "Completado".equalsIgnoreCase(reto.getStatus())) {
                    int completedCount = database.retoDao().getCompletedRetosCount(reto.getUserId());
                    GamificationManager gm = new GamificationManager(this);
                    gm.awardChallengeCompletion(reto.getUserId(), reto.getCategory(), completedCount, reto.getTitle());
                    gm.checkAchievements(reto.getUserId());
                    gm.release();
                }
                runOnUiThread(() -> {
                    Toast.makeText(this, getString(R.string.challenge_updated), Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, getString(R.string.error_challenge_save), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void deleteReto() {
        if (reto == null) return;
        new Thread(() -> {
            try {
                database.retoDao().deleteReto(reto);
                runOnUiThread(() -> {
                    Toast.makeText(this, "Reto eliminado", Toast.LENGTH_SHORT).show();
                    Intent data = new Intent();
                    data.putExtra("reto_eliminado", true);
                    data.putExtra("reto_id", reto.getId());
                    setResult(RESULT_OK, data);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(this, "Error eliminando reto", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showDateTimePicker() {
        final Calendar now = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
            final Calendar picked = Calendar.getInstance();
            picked.set(Calendar.YEAR, year);
            picked.set(Calendar.MONTH, month);
            picked.set(Calendar.DAY_OF_MONTH, day);
            new TimePickerDialog(this, (v, h, m) -> {
                picked.set(Calendar.HOUR_OF_DAY, h);
                picked.set(Calendar.MINUTE, m);
                long ms = picked.getTimeInMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", new Locale("es"));
                if (etDeadline != null) {
                    etDeadline.setText(sdf.format(ms));
                    etDeadline.setTag(ms);
                }
            }, now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true).show();
        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        dp.show();
    }
}
