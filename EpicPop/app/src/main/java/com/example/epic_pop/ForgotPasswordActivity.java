package com.example.epic_pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epic_pop.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilEmailReset;
    private TextInputEditText etEmailReset;
    private MaterialButton btnSendReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Aplicar tema actual
        ConstraintLayout root = findViewById(R.id.root_forgot_password);
        ThemeUtils.applyToActivity(this, root);

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();
    }

    private void initViews() {
        tilEmailReset = findViewById(R.id.til_email_reset);
        etEmailReset = findViewById(R.id.et_email_reset);
        btnSendReset = findViewById(R.id.btn_send_reset);
    }

    private void setupListeners() {
        // Botón de regreso
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                // Animación suave al volver
                v.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                        finish();
                    })
                    .start();
            });
        }

        // Botón enviar enlace de recuperación
        if (btnSendReset != null) {
            btnSendReset.setOnClickListener(v -> sendResetEmail());
        }

        // Enlace volver al login
        TextView tvBackToLogin = findViewById(R.id.tv_back_to_login);
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> {
                // Animación y navegar al login
                v.animate()
                    .alpha(0.7f)
                    .setDuration(150)
                    .withEndAction(() -> {
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    })
                    .start();
            });
        }
    }

    private void sendResetEmail() {
        // Limpiar errores previos
        if (tilEmailReset != null) {
            tilEmailReset.setError(null);
        }

        String email = etEmailReset != null ? etEmailReset.getText().toString().trim() : "";

        // Validar email
        if (TextUtils.isEmpty(email)) {
            if (tilEmailReset != null) {
                tilEmailReset.setError("Por favor, ingresa tu correo electrónico");
            }
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            if (tilEmailReset != null) {
                tilEmailReset.setError("Por favor, ingresa un correo electrónico válido");
            }
            return;
        }

        // Animación del botón mientras "envía"
        if (btnSendReset != null) {
            btnSendReset.setEnabled(false);
            btnSendReset.setText("Enviando...");

            btnSendReset.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(() -> {
                    btnSendReset.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(100)
                        .withEndAction(() -> {
                            // Simular envío de email (en una app real, aquí harías la llamada al servidor)
                            simulateEmailSent(email);
                        })
                        .start();
                })
                .start();
        }
    }

    private void simulateEmailSent(String email) {
        // Simular un pequeño delay para que parezca real
        new android.os.Handler(getMainLooper()).postDelayed(() -> {
            // Restaurar botón
            if (btnSendReset != null) {
                btnSendReset.setEnabled(true);
                btnSendReset.setText("Enviar enlace de recuperación");
            }

            // Mostrar mensaje de éxito
            Toast.makeText(this,
                "Se ha enviado un enlace de recuperación a " + email,
                Toast.LENGTH_LONG).show();

            // Opcional: regresar a la pantalla de login después de un momento
            new android.os.Handler(getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }, 2000);

        }, 1500); // Simular delay de 1.5 segundos
    }
}
