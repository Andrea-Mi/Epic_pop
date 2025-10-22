package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "EpicPop";
    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegister, tvForgotPassword;
    private TextInputLayout tilEmail, tilPassword;
    private EpicPopDatabase database;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "LoginActivity iniciando...");

        try {
            setContentView(R.layout.activity_login);
            Log.d(TAG, "Layout del login cargado exitosamente");

            database = EpicPopDatabase.getDatabase(this);
            executor = Executors.newSingleThreadExecutor();

            initViews();
            setupClickListeners();

        } catch (Exception e) {
            Log.e(TAG, "Error en LoginActivity onCreate", e);
        }
    }

    private void initViews() {
        try {
            etEmail = findViewById(R.id.et_email);
            etPassword = findViewById(R.id.et_password);
            btnLogin = findViewById(R.id.btn_login);
            tvRegister = findViewById(R.id.tv_register);
            tvForgotPassword = findViewById(R.id.tv_forgot_password);
            tilEmail = findViewById(R.id.til_email);
            tilPassword = findViewById(R.id.til_password);
            Log.d(TAG, "Vistas del login inicializadas correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error inicializando vistas en LoginActivity", e);
        }
    }

    private void setupClickListeners() {
        try {
            btnLogin.setOnClickListener(v -> attemptLogin());

            tvRegister.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            });

            tvForgotPassword.setOnClickListener(v -> {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            });

            Log.d(TAG, "Click listeners configurados correctamente");
        } catch (Exception e) {
            Log.e(TAG, "Error configurando click listeners", e);
        }
    }

    private void attemptLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Limpiar errores previos
        tilEmail.setError(null);
        tilPassword.setError(null);

        // Validaciones
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

        // Realizar login en background usando ExecutorService moderno
        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        executor.execute(() -> {
            Usuario usuario = null;
            try {
                Log.d(TAG, "Verificando credenciales para: " + email);

                // Obtener usuario por email
                usuario = database.usuarioDao().getUsuarioByEmail(email);
                if (usuario != null) {
                    Log.d(TAG, "Usuario encontrado: " + usuario.getUsername());

                    // Verificar contraseña usando el salt almacenado
                    String storedHash = usuario.getPassword();
                    String storedSalt = usuario.getSalt();

                    if (storedSalt != null && SecurityUtils.verifyPassword(password, storedHash, storedSalt)) {
                        Log.d(TAG, "Credenciales válidas");
                        updateLastLogin(usuario.getId());
                    } else {
                        Log.d(TAG, "Credenciales inválidas");
                        usuario = null;
                    }
                } else {
                    Log.d(TAG, "Usuario no encontrado");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error durante login", e);
                usuario = null;
            }

            // Volver al hilo principal para actualizar UI
            final Usuario finalUsuario = usuario;
            runOnUiThread(() -> {
                btnLogin.setEnabled(true);
                btnLogin.setText("Iniciar Sesión");

                if (finalUsuario != null) {
                    // Guardar sesión
                    SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
                    prefs.edit()
                        .putInt("user_id", finalUsuario.getId())
                        .putString("username", finalUsuario.getUsername())
                        .putBoolean("is_logged_in", true)
                        .apply();

                    Toast.makeText(LoginActivity.this, "¡Bienvenido " + finalUsuario.getUsername() + "!", Toast.LENGTH_SHORT).show();

                    // Ir a MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void updateLastLogin(int userId) {
        executor.execute(() -> {
            try {
                database.usuarioDao().updateLastLogin(userId, String.valueOf(System.currentTimeMillis()));
            } catch (Exception e) {
                Log.e(TAG, "Error actualizando último login", e);
            }
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
