package com.example.epic_pop;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MusicPlayerActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());

        // TODO: Implementar reproductor completo con playlists y controles
    }
}
