package com.example.epic_pop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AvatarCustomizerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar_customizer);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // TODO: Implementar personalización de avatar con elementos desbloqueables
    }
}
