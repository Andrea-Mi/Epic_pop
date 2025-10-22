package com.example.epic_pop;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.epic_pop.utils.ThemeUtils;

public class ThemeSettingsActivity extends AppCompatActivity {

    private ImageView checkPurple, checkYellow, checkGreen;
    private View cardPurple, cardYellow, cardGreen;
    private ConstraintLayout root;
    private String currentTheme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme_settings);

        // Referencias de vistas
        root = findViewById(R.id.root_theme);
        checkPurple = findViewById(R.id.theme_purple_check);
        checkYellow = findViewById(R.id.theme_yellow_check);
        checkGreen = findViewById(R.id.theme_green_check);
        cardPurple = findViewById(R.id.theme_purple_card);
        cardYellow = findViewById(R.id.theme_yellow_card);
        cardGreen = findViewById(R.id.theme_green_card);
        ImageButton btnBack = findViewById(R.id.btn_back);

        // Botón de regreso
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Inicializa estado actual
        currentTheme = ThemeUtils.getTheme(this);
        updateCheckVisibility(currentTheme);

        // Listeners de tema
        if (cardPurple != null) {
            cardPurple.setOnClickListener(v -> applyTheme("purple"));
        }
        if (cardYellow != null) {
            cardYellow.setOnClickListener(v -> applyTheme("yellow"));
        }
        if (cardGreen != null) {
            cardGreen.setOnClickListener(v -> applyTheme("green"));
        }
    }

    private void updateCheckVisibility(String theme) {
        if (checkPurple != null) {
            checkPurple.setVisibility(theme.equals("purple") ? View.VISIBLE : View.GONE);
        }
        if (checkYellow != null) {
            checkYellow.setVisibility(theme.equals("yellow") ? View.VISIBLE : View.GONE);
        }
        if (checkGreen != null) {
            checkGreen.setVisibility(theme.equals("green") ? View.VISIBLE : View.GONE);
        }
    }

    private void applyTheme(String theme) {
        if (theme.equals(currentTheme)) return;

        // Guarda el tema
        ThemeUtils.saveTheme(this, theme);
        currentTheme = theme;

        // Actualiza los checks
        updateCheckVisibility(theme);

        // Aplica el cambio con fade suave
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setDuration(150);
        fadeOut.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Cambia el fondo y barra de estado
                ThemeUtils.applyToActivity(ThemeSettingsActivity.this, root);

                // Anima de vuelta
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setDuration(150);
                fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
                if (root != null) root.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        if (root != null) root.startAnimation(fadeOut);
    }
}
