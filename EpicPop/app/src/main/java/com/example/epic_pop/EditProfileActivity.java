package com.example.epic_pop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.epic_pop.database.EpicPopDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etNewUsername;
    private Button btnSave, btnCancel;
    private ImageButton btnBack;
    private int userId;
    private EpicPopDatabase db;
    private ExecutorService executor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        db = EpicPopDatabase.getDatabase(this);
        executor = Executors.newSingleThreadExecutor();
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        etNewUsername = findViewById(R.id.et_new_username);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        btnCancel.setOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> attemptSave());
    }

    private void attemptSave() {
        String newName = etNewUsername.getText().toString().trim();
        if (TextUtils.isEmpty(newName) || newName.length() < 3) {
            Toast.makeText(this, getString(R.string.edit_profile_name_invalid), Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == -1) {
            Toast.makeText(this, "Usuario inválido", Toast.LENGTH_SHORT).show();
            return;
        }
        executor.execute(() -> {
            try {
                db.usuarioDao().updateUsername(userId, newName);
                SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
                prefs.edit().putString("username", newName).apply();
                runOnUiThread(() -> {
                    Toast.makeText(EditProfileActivity.this, getString(R.string.edit_profile_name_updated), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this, "Error actualizando nombre", Toast.LENGTH_SHORT).show());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdown();
    }
}
