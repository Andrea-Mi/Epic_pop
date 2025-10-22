package com.example.epic_pop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.epic_pop.utils.ThemeUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {

    private static final String FEEDBACK_PREFS = "feedback_prefs";
    private static final String KEY_FEEDBACK_HISTORY = "feedback_history";

    private EditText etFeedback;
    private TextInputLayout tilFeedback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        // Aplicar tema actual
        View root = findViewById(R.id.root_feedback);
        ThemeUtils.applyToActivity(this, root);

        // Inicializar vistas
        etFeedback = findViewById(R.id.et_feedback);
        tilFeedback = findViewById(R.id.til_feedback);

        ImageButton btnBack = findViewById(R.id.btn_back);
        MaterialButton btnSendFeedback = findViewById(R.id.btn_send_feedback);
        MaterialButton btnFAQ = findViewById(R.id.btn_faq);
        MaterialButton btnContactSupport = findViewById(R.id.btn_contact_support);

        // Configurar botones
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnSendFeedback != null) {
            btnSendFeedback.setOnClickListener(v -> submitFeedback());
        }

        if (btnFAQ != null) {
            btnFAQ.setOnClickListener(v -> showFAQ());
        }

        if (btnContactSupport != null) {
            btnContactSupport.setOnClickListener(v -> contactSupport());
        }
    }

    private void submitFeedback() {
        String feedback = etFeedback.getText() != null ? etFeedback.getText().toString().trim() : "";

        if (TextUtils.isEmpty(feedback)) {
            if (tilFeedback != null) {
                tilFeedback.setError(getString(R.string.feedback_empty));
            }
            return;
        }

        // Guardar feedback con timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        String entry = timestamp + ": " + feedback;

        // Guardar en SharedPreferences
        SharedPreferences prefs = getSharedPreferences(FEEDBACK_PREFS, MODE_PRIVATE);
        String history = prefs.getString(KEY_FEEDBACK_HISTORY, "");

        if (!TextUtils.isEmpty(history)) {
            history = entry + "\n\n" + history;
        } else {
            history = entry;
        }

        prefs.edit().putString(KEY_FEEDBACK_HISTORY, history).apply();

        // Mostrar mensaje de agradecimiento
        new AlertDialog.Builder(this)
                .setTitle(R.string.feedback_thanks)
                .setMessage(getString(R.string.feedback_thanks))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }

    private void showFAQ() {
        // Abrir la nueva actividad de preguntas frecuentes
        Intent intent = new Intent(this, FAQActivity.class);
        startActivity(intent);
    }

    private void contactSupport() {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:soporte@epicpop.com"));
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_contact_subject));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de correo", Toast.LENGTH_SHORT).show();
        }
    }
}
