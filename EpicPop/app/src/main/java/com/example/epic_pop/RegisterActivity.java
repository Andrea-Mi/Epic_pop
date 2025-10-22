package com.example.epic_pop;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.models.Usuario;
import com.example.epic_pop.utils.SecurityUtils;
import com.google.android.material.textfield.TextInputLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLogin;
    private TextInputLayout tilUsername, tilEmail, tilPassword, tilConfirmPassword;
    private EpicPopDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "RegisterActivity iniciando...");

        try {
            setContentView(R.layout.activity_register);

            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();

            initViews();
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error en RegisterActivity onCreate", e);
        }
    }

    private void initViews() {
        try {
            etUsername = findViewById(R.id.et_username);
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            etConfirmPassword = findViewById(R.id.et_confirm_password);
            btnRegister = findViewById(R.id.btn_register);
            tvLogin = findViewById(R.id.tv_login);
            tilUsername = findViewById(R.id.til_username);
            tilEmail = findViewById(R.id.til_email);
            tilPassword = findViewById(R.id.til_password);
            tilConfirmPassword = findViewById(R.id.til_confirm_password);
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas en RegisterActivity", e);
        }
    }

    private void setupClickListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());

        tvLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Limpiar errores previos
        tilUsername.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        // Validaciones
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("El nombre de usuario es requerido");
            etUsername.requestFocus();
            return;
        }

        if (username.length() < 3) {
            tilUsername.setError("El nombre debe tener al menos 3 caracteres");
            etUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            tilEmail.setError("El email es requerido");
            etEmail.requestFocus();
            return;
        }

        if (!SecurityUtils.isValidEmail(email)) {
            tilEmail.setError("Email no válido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            tilPassword.setError("La contraseña es requerida");
            etPassword.requestFocus();
            return;
        }

        if (!SecurityUtils.isValidPassword(password)) {
            tilPassword.setError("La contraseña debe tener al menos 8 caracteres, incluir mayúsculas, minúsculas, números y símbolos");
            etPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("Confirma tu contraseña");
            etConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        // Realizar registro usando ExecutorService moderno
        performRegister(username, email, password);
    }

    private void performRegister(String username, String email, String password) {
        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        executor.execute(() -> {
            boolean success = false;
            String errorMessage = null;

            try {
                // Verificar si el email ya existe
                int emailExists = database.usuarioDao().checkEmailExists(email);
                if (emailExists > 0) {
                    errorMessage = "Ya existe una cuenta con este email";
                } else {
                    // Generar salt y cifrar contraseña
                    String salt = SecurityUtils.generateSalt();
                    String hashedPassword = SecurityUtils.hashPassword(password, salt);

                    // Crear nuevo usuario
                    Usuario usuario = new Usuario(username, email, hashedPassword);
                    usuario.setSalt(salt);

                    // Insertar en base de datos
                    long userId = database.usuarioDao().insertUsuario(usuario);
                    success = userId > 0;

                    Log.d(TAG, "Usuario registrado con ID: " + userId);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error durante registro", e);
                errorMessage = "Error al registrar usuario";
            }

            // Volver al hilo principal
            final boolean finalSuccess = success;
            final String finalErrorMessage = errorMessage;

            runOnUiThread(() -> {
                btnRegister.setEnabled(true);
                btnRegister.setText("Registrarse");

                if (finalSuccess) {
                    Toast.makeText(RegisterActivity.this, "¡Registro exitoso! Ya puedes iniciar sesión", Toast.LENGTH_SHORT).show();

                    // Ir a LoginActivity
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(RegisterActivity.this, finalErrorMessage != null ? finalErrorMessage : "Error al registrar", Toast.LENGTH_SHORT).show();
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
