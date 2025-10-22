package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epic_pop.utils.ThemeUtils;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // Cargar nombre de usuario
        loadProfileData();

        // Configurar tema
        ConstraintLayout rootView = findViewById(R.id.root_settings);
        ThemeUtils.applyToActivity(this, rootView);

        // Configurar sección de perfil
        View profileSection = findViewById(R.id.section_profile);
        if (profileSection != null) {
            profileSection.setOnClickListener(v -> startActivity(new Intent(this, ProfileActivity.class)));
        }

        // Configurar opciones
        View rowNotifications = findViewById(R.id.row_notifications);
        View rowTheme = findViewById(R.id.row_theme);
        View rowPrivacy = findViewById(R.id.row_privacy);
        View rowHelp = findViewById(R.id.row_help);
        View rowLogout = findViewById(R.id.row_logout);
        View mascot = findViewById(R.id.iv_mascot);

        // Configurar clicks en opciones
        if (rowNotifications != null) {
            rowNotifications.setOnClickListener(v -> openNotificationsSettings());
        }

        if (rowTheme != null) {
            rowTheme.setOnClickListener(v -> openThemeSettings());
        }

        if (rowPrivacy != null) {
            rowPrivacy.setOnClickListener(v -> openPrivacySecurity());
        }

        if (rowHelp != null) {
            rowHelp.setOnClickListener(v -> openFeedback());
        }

        if (rowLogout != null) {
            rowLogout.setOnClickListener(v -> showLogoutDialog());
        }

        // Animación mascota
        if (mascot != null) {
            mascot.setOnClickListener(v -> v.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setDuration(150)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(150).setInterpolator(new AccelerateDecelerateInterpolator()).start())
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .start());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Aplicar el tema cada vez que volvemos a la pantalla (por si cambió en ThemeSettings)
        ConstraintLayout rootView = findViewById(R.id.root_settings);
        ThemeUtils.applyToActivity(this, rootView);
    }

    private void loadProfileData() {
        TextView tvName = findViewById(R.id.tv_profile_name);
        if (tvName != null) {
            SharedPreferences prefs = getSharedPreferences(ThemeUtils.PREFS, MODE_PRIVATE);
            String username = prefs.getString("username", getString(R.string.settings_profile_name));
            tvName.setText(username);
        }
    }

    private void openNotificationsSettings() {
        startActivity(new Intent(this, NotificationSettingsActivity.class));
    }

    private void openThemeSettings() {
        startActivity(new Intent(this, ThemeSettingsActivity.class));
    }

    private void openFeedback() {
        startActivity(new Intent(this, FeedbackActivity.class));
    }

    private void openPrivacySecurity() {
        startActivity(new Intent(this, PrivacySecurityActivity.class));
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.opt_logout)
                .setMessage(R.string.settings_logout_confirm)
                .setNegativeButton(R.string.action_cancel, (d, w) -> d.dismiss())
                .setPositiveButton(R.string.settings_logout_ok, (dialog, which) -> {
                    // Clear session and go to login
                    SharedPreferences prefs = getSharedPreferences(ThemeUtils.PREFS, MODE_PRIVATE);
                    prefs.edit().clear().apply();
                    Intent i = new Intent(this, LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    finish();
                })
                .show();
    }
}
