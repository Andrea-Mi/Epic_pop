package com.example.epic_pop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.epic_pop.database.EpicPopDatabase;
import com.example.epic_pop.database.UsuarioDao;
import com.example.epic_pop.models.Usuario;
import com.example.epic_pop.utils.SecurityUtils;
import com.example.epic_pop.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputLayout tilCurrentPassword;
    private TextInputLayout tilNewPassword;
    private TextInputEditText etCurrentPassword;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;
    private TextView tvErrorMessage;
    private TextView tvPasswordStrength;
    private MaterialButton btnSavePassword;
    private View passwordStrengthLayout;
    private View strengthBar1, strengthBar2, strengthBar3, strengthBar4;

    private boolean isCurrentPasswordValid = false;
    private boolean isPasswordValid = false;
    private boolean isConfirmPasswordValid = false;

    private Usuario currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        initViews();
        loadCurrentUser();
        setupListeners();
        setupRealtimeValidation();
    }

    private void initViews() {
        tilCurrentPassword = findViewById(R.id.til_current_password);
        tilNewPassword = findViewById(R.id.til_new_password);
        etCurrentPassword = findViewById(R.id.et_current_password);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        btnSavePassword = findViewById(R.id.btn_save_password);
        passwordStrengthLayout = findViewById(R.id.password_strength_layout);
        strengthBar1 = findViewById(R.id.strength_bar_1);
        strengthBar2 = findViewById(R.id.strength_bar_2);
        strengthBar3 = findViewById(R.id.strength_bar_3);
        strengthBar4 = findViewById(R.id.strength_bar_4);
    }

    private void loadCurrentUser() {
        SharedPreferences prefs = getSharedPreferences(ThemeUtils.PREFS, MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        if (userId != -1) {
            try {
                EpicPopDatabase db = EpicPopDatabase.getDatabase(this);
                UsuarioDao dao = db.usuarioDao();
                currentUser = dao.getUsuarioById(userId);
            } catch (Exception e) {
                Toast.makeText(this, "Error cargando usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupListeners() {
        ImageButton btnCancel = findViewById(R.id.btn_cancel);
        if (btnCancel != null) {
            btnCancel.setOnClickListener(v -> finish());
        }
        if (btnSavePassword != null) {
            btnSavePassword.setOnClickListener(v -> saveNewPassword());
        }
    }

    private void setupRealtimeValidation() {
        if (etCurrentPassword != null) {
            etCurrentPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateCurrentPassword();
                    checkFormValidity();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        if (etNewPassword != null) {
            etNewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateNewPassword(s.toString());
                    validateConfirmPassword();
                    checkFormValidity();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
        if (etConfirmPassword != null) {
            etConfirmPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    validateConfirmPassword();
                    checkFormValidity();
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }

    private void validateCurrentPassword() {
        if (etCurrentPassword == null || tilCurrentPassword == null) return;
        String input = etCurrentPassword.getText() != null ? etCurrentPassword.getText().toString() : "";
        if (TextUtils.isEmpty(input)) {
            tilCurrentPassword.setError(null);
            isCurrentPasswordValid = false;
            return;
        }
        if (currentUser == null || currentUser.getPassword() == null || currentUser.getSalt() == null) {
            tilCurrentPassword.setError(getString(R.string.error_current_password_incorrect));
            isCurrentPasswordValid = false;
            return;
        }
        boolean match = SecurityUtils.verifyPassword(input, currentUser.getPassword(), currentUser.getSalt());
        if (!match) {
            tilCurrentPassword.setError(getString(R.string.error_current_password_incorrect));
            isCurrentPasswordValid = false;
        } else {
            tilCurrentPassword.setError(null);
            isCurrentPasswordValid = true;
        }
    }

    private void validateNewPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            hidePasswordStrength();
            isPasswordValid = false;
            return;
        }
        showPasswordStrength();
        int strength = calculatePasswordStrength(password);
        updatePasswordStrengthUI(strength);
        isPasswordValid = SecurityUtils.isValidPassword(password);
        if (tilNewPassword != null) {
            if (!isPasswordValid) {
                tilNewPassword.setError(getString(R.string.password_too_weak));
            } else {
                tilNewPassword.setError(null);
            }
        }
    }

    private void validateConfirmPassword() {
        String newPassword = etNewPassword != null && etNewPassword.getText() != null ? etNewPassword.getText().toString() : "";
        String confirmPassword = etConfirmPassword != null && etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString() : "";
        if (TextUtils.isEmpty(confirmPassword)) {
            hideErrorMessage();
            isConfirmPasswordValid = false;
            return;
        }
        isConfirmPasswordValid = newPassword.equals(confirmPassword) && !TextUtils.isEmpty(newPassword);
        if (!isConfirmPasswordValid) {
            showErrorMessage(getString(R.string.passwords_not_match));
        } else {
            hideErrorMessage();
        }
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        if (password.length() >= 8) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[0-9].*")) strength++;
        return Math.min(strength, 4);
    }

    private void updatePasswordStrengthUI(int strength) {
        resetStrengthBars();
        String strengthText;
        int strengthColor;
        switch (strength) {
            case 0:
            case 1:
                strengthText = getString(R.string.password_strength_weak);
                strengthColor = android.R.color.holo_red_dark;
                if (strengthBar1 != null) strengthBar1.setBackgroundColor(getColor(strengthColor));
                break;
            case 2:
                strengthText = getString(R.string.password_strength_fair);
                strengthColor = android.R.color.holo_orange_dark;
                if (strengthBar1 != null) strengthBar1.setBackgroundColor(getColor(strengthColor));
                if (strengthBar2 != null) strengthBar2.setBackgroundColor(getColor(strengthColor));
                break;
            case 3:
                strengthText = getString(R.string.password_strength_good);
                strengthColor = android.R.color.holo_blue_dark;
                if (strengthBar1 != null) strengthBar1.setBackgroundColor(getColor(strengthColor));
                if (strengthBar2 != null) strengthBar2.setBackgroundColor(getColor(strengthColor));
                if (strengthBar3 != null) strengthBar3.setBackgroundColor(getColor(strengthColor));
                break;
            case 4:
                strengthText = getString(R.string.password_strength_strong);
                strengthColor = android.R.color.holo_green_dark;
                if (strengthBar1 != null) strengthBar1.setBackgroundColor(getColor(strengthColor));
                if (strengthBar2 != null) strengthBar2.setBackgroundColor(getColor(strengthColor));
                if (strengthBar3 != null) strengthBar3.setBackgroundColor(getColor(strengthColor));
                if (strengthBar4 != null) strengthBar4.setBackgroundColor(getColor(strengthColor));
                break;
            default:
                strengthText = getString(R.string.password_requirements);
                strengthColor = android.R.color.darker_gray;
        }
        if (tvPasswordStrength != null) {
            tvPasswordStrength.setText(strengthText);
            tvPasswordStrength.setTextColor(getColor(strengthColor));
        }
    }

    private void resetStrengthBars() {
        int defaultColor = getColor(android.R.color.darker_gray);
        if (strengthBar1 != null) strengthBar1.setBackgroundColor(defaultColor);
        if (strengthBar2 != null) strengthBar2.setBackgroundColor(defaultColor);
        if (strengthBar3 != null) strengthBar3.setBackgroundColor(defaultColor);
        if (strengthBar4 != null) strengthBar4.setBackgroundColor(defaultColor);
    }

    private void showPasswordStrength() {
        if (passwordStrengthLayout != null) passwordStrengthLayout.setVisibility(View.VISIBLE);
        if (tvPasswordStrength != null) tvPasswordStrength.setVisibility(View.VISIBLE);
    }

    private void hidePasswordStrength() {
        if (passwordStrengthLayout != null) passwordStrengthLayout.setVisibility(View.GONE);
        if (tvPasswordStrength != null) tvPasswordStrength.setVisibility(View.GONE);
    }

    private void showErrorMessage(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }

    private void hideErrorMessage() {
        if (tvErrorMessage != null) tvErrorMessage.setVisibility(View.GONE);
    }

    private void checkFormValidity() {
        boolean isFormValid = isCurrentPasswordValid && isPasswordValid && isConfirmPasswordValid;
        if (btnSavePassword != null) {
            btnSavePassword.setEnabled(isFormValid);
            btnSavePassword.setAlpha(isFormValid ? 1.0f : 0.5f);
        }
    }

    private void saveNewPassword() {
        validateCurrentPassword();
        validateNewPassword(etNewPassword != null && etNewPassword.getText()!=null ? etNewPassword.getText().toString(): "");
        validateConfirmPassword();
        if (!(isCurrentPasswordValid && isPasswordValid && isConfirmPasswordValid)) {
            Toast.makeText(this, getString(R.string.error_invalid_form), Toast.LENGTH_SHORT).show();
            return;
        }
        String newPassword = etNewPassword != null && etNewPassword.getText()!=null ? etNewPassword.getText().toString(): "";
        if (currentUser == null) {
            Toast.makeText(this, "Error: usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            EpicPopDatabase db = EpicPopDatabase.getDatabase(this);
            UsuarioDao usuarioDao = db.usuarioDao();
            String salt = SecurityUtils.generateSalt();
            String hashed = SecurityUtils.hashPassword(newPassword, salt);
            currentUser.setSalt(salt);
            currentUser.setPassword(hashed);
            usuarioDao.updateUsuario(currentUser);
            if (etCurrentPassword != null) etCurrentPassword.setText("");
            if (etNewPassword != null) etNewPassword.setText("");
            if (etConfirmPassword != null) etConfirmPassword.setText("");
            Toast.makeText(this, getString(R.string.password_changed_success), Toast.LENGTH_LONG).show();
            new Handler().postDelayed(this::finish, 1000);
        } catch (Exception e) {
            Toast.makeText(this, "Error al actualizar contraseña", Toast.LENGTH_SHORT).show();
        }
    }
}
