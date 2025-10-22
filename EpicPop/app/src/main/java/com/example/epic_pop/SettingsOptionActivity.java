package com.example.epic_pop;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsOptionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_option);

        String title = getIntent().getStringExtra("title");
        TextView tv = findViewById(R.id.tv_option_title);
        if (title != null) tv.setText(title);

        ImageButton back = findViewById(R.id.btn_back);
        back.setOnClickListener(v -> finish());
    }
}
