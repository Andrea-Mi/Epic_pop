package com.example.epic_pop;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epic_pop.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;

public class PrivacySecurityActivity extends AppCompatActivity {

    private static final String TAG = "PrivacySecurity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_privacy_security);
        } catch (Exception e) {
            Log.e(TAG, "Error inflando layout privacy_security", e);
            Toast.makeText(this, "Error al cargar pantalla", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ConstraintLayout root = findViewById(R.id.root_privacy_security);
        if (root != null) {
            try { ThemeUtils.applyToActivity(this, root); } catch (Exception ex) { Log.w(TAG, "Tema no aplicado", ex); }
        }

        View btnBack = findViewById(R.id.btn_back_privacy);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        } else {
            Log.w(TAG, "btn_back_privacy es null");
        }

        MaterialButton btnChangePassword = findViewById(R.id.btn_change_password_ps);
        if (btnChangePassword != null) {
            btnChangePassword.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, ChangePasswordActivity.class));
                } catch (Exception ex) {
                    Log.e(TAG, "Error lanzando ChangePasswordActivity", ex);
                    Toast.makeText(this, "No se pudo abrir Cambiar contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w(TAG, "btn_change_password_ps es null");
        }

        MaterialButton btnRecoverPassword = findViewById(R.id.btn_recover_password_ps);
        if (btnRecoverPassword != null) {
            btnRecoverPassword.setOnClickListener(v -> {
                try {
                    startActivity(new Intent(this, ForgotPasswordActivity.class));
                } catch (Exception ex) {
                    Log.e(TAG, "Error lanzando ForgotPasswordActivity", ex);
                    Toast.makeText(this, "No se pudo abrir Recuperar contraseña", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.w(TAG, "btn_recover_password_ps es null");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout root = findViewById(R.id.root_privacy_security);
        if (root != null) {
            try { ThemeUtils.applyToActivity(this, root); } catch (Exception ex) { Log.w(TAG, "Tema no aplicado en onResume", ex); }
        }
    }
}
