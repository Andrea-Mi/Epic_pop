package com.example.epic_pop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epic_pop.utils.NotificationUtils;
import com.example.epic_pop.utils.ThemeUtils;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class NotificationSettingsActivity extends AppCompatActivity {
    private SwitchMaterial switchNotifications;
    private SwitchMaterial switchChallengeReminders;
    private SwitchMaterial switchAchievements;
    private SwitchMaterial switchDailyMotivation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        // Aplicar tema actual
        ConstraintLayout root = findViewById(R.id.root_notification);
        ThemeUtils.applyToActivity(this, root);

        // Botón de regreso
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Inicializar switches
        switchNotifications = findViewById(R.id.switch_notifications_master);
        switchChallengeReminders = findViewById(R.id.switch_challenge_reminders);
        switchAchievements = findViewById(R.id.switch_achievements);
        switchDailyMotivation = findViewById(R.id.switch_daily_motivation);

        // Cargar estados actuales
        loadCurrentSettings();

        // Configurar listeners
        if (switchNotifications != null) {
            switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
                // Habilitar/deshabilitar opciones específicas según el master switch
                enableSpecificOptions(isChecked);
            });
        }

        // Botón guardar
        View btnSave = findViewById(R.id.btn_save);
        if (btnSave != null) {
            btnSave.setOnClickListener(v -> saveSettings());
        }
    }

    private void loadCurrentSettings() {
        if (switchNotifications != null) {
            switchNotifications.setChecked(NotificationUtils.isEnabled(this));
        }

        // Cargar configuraciones específicas
        if (switchChallengeReminders != null) {
            switchChallengeReminders.setChecked(NotificationUtils.isChallengeRemindersEnabled(this));
        }

        if (switchAchievements != null) {
            switchAchievements.setChecked(NotificationUtils.isAchievementsEnabled(this));
        }

        if (switchDailyMotivation != null) {
            switchDailyMotivation.setChecked(NotificationUtils.isDailyMotivationEnabled(this));
        }

        // Aplicar estado inicial
        enableSpecificOptions(NotificationUtils.isEnabled(this));
    }

    private void enableSpecificOptions(boolean enabled) {
        if (switchChallengeReminders != null) {
            switchChallengeReminders.setEnabled(enabled);
            switchChallengeReminders.setAlpha(enabled ? 1.0f : 0.5f);
        }

        if (switchAchievements != null) {
            switchAchievements.setEnabled(enabled);
            switchAchievements.setAlpha(enabled ? 1.0f : 0.5f);
        }

        if (switchDailyMotivation != null) {
            switchDailyMotivation.setEnabled(enabled);
            switchDailyMotivation.setAlpha(enabled ? 1.0f : 0.5f);
        }
    }

    private void saveSettings() {
        if (switchNotifications != null) {
            boolean notificationsEnabled = switchNotifications.isChecked();
            NotificationUtils.setEnabled(this, notificationsEnabled);

            // Guardar configuraciones específicas solo si las notificaciones están habilitadas
            if (notificationsEnabled) {
                if (switchChallengeReminders != null) {
                    NotificationUtils.setChallengeRemindersEnabled(this, switchChallengeReminders.isChecked());
                }

                if (switchAchievements != null) {
                    NotificationUtils.setAchievementsEnabled(this, switchAchievements.isChecked());
                }

                if (switchDailyMotivation != null) {
                    NotificationUtils.setDailyMotivationEnabled(this, switchDailyMotivation.isChecked());
                }
            } else {
                // Si las notificaciones están deshabilitadas, deshabilitar todas las opciones específicas
                NotificationUtils.setChallengeRemindersEnabled(this, false);
                NotificationUtils.setAchievementsEnabled(this, false);
                NotificationUtils.setDailyMotivationEnabled(this, false);
            }

            // Mostrar confirmación
            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
