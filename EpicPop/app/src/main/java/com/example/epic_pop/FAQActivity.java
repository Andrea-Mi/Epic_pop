package com.example.epic_pop;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.epic_pop.models.FAQItem;
import com.example.epic_pop.utils.ThemeUtils;

import java.util.ArrayList;
import java.util.List;

public class FAQActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        // Aplicar tema actual
        ConstraintLayout root = findViewById(R.id.root_faq);
        ThemeUtils.applyToActivity(this, root);

        // Botón de regreso
        ImageButton btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Configurar RecyclerView con preguntas frecuentes
        RecyclerView recyclerView = findViewById(R.id.rv_faq);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new FAQAdapter(getFAQItems()));
        }
    }

    private List<FAQItem> getFAQItems() {
        List<FAQItem> faqItems = new ArrayList<>();

        faqItems.add(new FAQItem(
            "¿Cómo crear un nuevo reto?",
            "Para crear un nuevo reto, ve a la pantalla principal y toca el botón '+' flotante. Completa la información del reto como título, descripción, categoría y dificultad."
        ));

        faqItems.add(new FAQItem(
            "¿Cómo cambiar el tema de la aplicación?",
            "Ve a Configuración > Tema de la aplicación. Puedes elegir entre tres temas: Morado, Amarillo y Verde. El cambio se aplica inmediatamente."
        ));

        faqItems.add(new FAQItem(
            "¿Cómo desactivar las notificaciones?",
            "Ve a Configuración > Editar notificaciones. Puedes desactivar todas las notificaciones o elegir tipos específicos como recordatorios de retos, logros o motivación diaria."
        ));

        faqItems.add(new FAQItem(
            "¿Cómo marcar un reto como completado?",
            "En la lista de retos, toca el reto que quieres marcar como completado y selecciona la opción 'Completar reto'. También puedes hacerlo desde el calendario."
        ));

        faqItems.add(new FAQItem(
            "¿Qué son los logros y cómo obtenerlos?",
            "Los logros son recompensas que obtienes al completar retos, mantener rachas o alcanzar objetivos específicos. Puedes ver todos tus logros en la sección de Logros."
        ));

        faqItems.add(new FAQItem(
            "¿Cómo usar el asistente de IA?",
            "El asistente de IA te puede ayudar con motivación, sugerencias de retos y consejos. Accede desde la pantalla principal tocando el ícono de chat con IA."
        ));

        faqItems.add(new FAQItem(
            "¿Cómo cambiar mi contraseña?",
            "Ve a Configuración > Privacidad y seguridad > Cambiar contraseña. También puedes usar la opción '¿Olvidaste tu contraseña?' en la pantalla de login."
        ));

        faqItems.add(new FAQItem(
            "¿Los datos se sincronizan en la nube?",
            "Actualmente, los datos se almacenan localmente en tu dispositivo. Asegúrate de hacer respaldos regulares y no desinstalar la aplicación para no perder tu progreso."
        ));

        return faqItems;
    }
}
