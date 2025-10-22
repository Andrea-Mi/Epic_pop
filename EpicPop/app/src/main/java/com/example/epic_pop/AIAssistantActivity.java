package com.example.epic_pop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.epic_pop.api.GroqApiService;
import com.example.epic_pop.utils.ApiKeyManager;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AIAssistantActivity extends AppCompatActivity {

    private RecyclerView rvChat;
    private EditText etMessage;
    private ImageButton btnSend;
    private LottieAnimationView lottieThinking;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private GroqApiService groqService;
    private ApiKeyManager apiKeyManager;
    private String username;
    private static final String SYSTEM_PROMPT = "Eres un asistente motivacional de EpicPop que ayuda a los usuarios con retos personales. Responde siempre en español de forma clara, concisa, empática y motivadora. Cuando sugieras retos incluye variedad (salud, aprendizaje, hábitos, creatividad) y ofrece pasos concretos. Si el usuario pide análisis del progreso, responde de forma alentadora.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initApiService();
        getUserData();
        initViews();
        setupRecyclerView();
        setupClickListeners();
        addWelcomeMessage();
    }

    private void initApiService() {
        apiKeyManager = new ApiKeyManager(this);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor(message ->
                android.util.Log.d("GroqHTTP", message));
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    // Añadimos cabeceras comunes (User-Agent opcional)
                    return chain.proceed(chain.request().newBuilder()
                            .header("User-Agent", "EpicPop-Android/1.0")
                            .build());
                })
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.groq.com/openai/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        groqService = retrofit.create(GroqApiService.class);
    }

    private void getUserData() {
        SharedPreferences prefs = getSharedPreferences("epic_pop_prefs", MODE_PRIVATE);
        username = prefs.getString("username", "Usuario");
    }

    private void initViews() {
        rvChat = findViewById(R.id.rv_chat);
        etMessage = findViewById(R.id.et_message);
        btnSend = findViewById(R.id.btn_send);
        lottieThinking = findViewById(R.id.lottie_thinking);

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatMessages);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());

        findViewById(R.id.btn_motivation).setOnClickListener(v ->
                sendPredefinedMessage("Dame un mensaje motivacional para seguir con mis retos"));

        findViewById(R.id.btn_suggest_challenge).setOnClickListener(v ->
                sendPredefinedMessage("Sugiéreme un nuevo reto personal interesante"));

        findViewById(R.id.btn_tips).setOnClickListener(v ->
                sendPredefinedMessage("Dame consejos para mantener la motivación diaria"));
    }

    private void addWelcomeMessage() {
        String welcomeMessage = "¡Hola " + username + "! 🎯\n\n" +
                "Soy tu asistente de IA de EpicPop. Estoy aquí para ayudarte con:\n" +
                "• Motivación personalizada\n" +
                "• Sugerencias de nuevos retos\n" +
                "• Consejos para mantener tus rachas\n" +
                "• Análisis de tu progreso\n\n" +
                "¿En qué te puedo ayudar hoy?";

        chatMessages.add(new ChatMessage(welcomeMessage, false));
        chatAdapter.notifyDataSetChanged();
    }

    private void sendMessage() {
        String message = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        sendUserMessage(message);
    }

    private void sendPredefinedMessage(String message) {
        sendUserMessage(message);
    }

    private void sendUserMessage(String message) {
        // Validar API Key
        String key = apiKeyManager.getGroqApiKey();
        if (key == null || key.trim().isEmpty()) {
            Toast.makeText(this, "API Key de Groq no configurada", Toast.LENGTH_LONG).show();
            return;
        }

        // Agregar mensaje del usuario
        chatMessages.add(new ChatMessage(message, true));
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();

        // Limpiar input
        etMessage.setText("");

        // Mostrar indicador de "pensando"
        showThinkingAnimation();

        // Enviar a Groq API
        sendToGroqAPI();
    }

    private void sendToGroqAPI() {
        // Construir historial de conversación limitado para evitar mensajes demasiado largos
        List<GroqApiService.GroqRequest.Message> history = new ArrayList<>();
        // Añadir system prompt primero
        history.add(new GroqApiService.GroqRequest.Message("system", SYSTEM_PROMPT));

        // Convertir últimos N mensajes (por ejemplo 12) a formato API
        int max = 12; // límite simple
        int start = Math.max(0, chatMessages.size() - max);
        for (int i = start; i < chatMessages.size(); i++) {
            ChatMessage cm = chatMessages.get(i);
            String role = cm.isUser() ? "user" : "assistant";
            // Evitar incluir mensajes de error repetitivos para no sesgar respuestas
            if (cm.getMessage().startsWith("Lo siento, no pude procesar")) continue;
            history.add(new GroqApiService.GroqRequest.Message(role, cm.getMessage()));
        }

        GroqApiService.GroqRequest request = new GroqApiService.GroqRequest(history);
        String authHeader = "Bearer " + apiKeyManager.getGroqApiKey();

        android.util.Log.d("EpicPop", "Enviando request con " + history.size() + " mensajes al modelo Groq");

        Call<GroqApiService.GroqResponse> call = groqService.generateChatCompletion(
                authHeader,
                "application/json",
                request
        );

        call.enqueue(new Callback<GroqApiService.GroqResponse>() {
            @Override
            public void onResponse(Call<GroqApiService.GroqResponse> call, Response<GroqApiService.GroqResponse> response) {
                hideThinkingAnimation();
                android.util.Log.d("EpicPop", "Código respuesta Groq: " + response.code());

                if (response.isSuccessful() && response.body() != null &&
                        response.body().getChoices() != null && response.body().getChoices().length > 0 &&
                        response.body().getChoices()[0].getMessage() != null) {

                    String aiMessage = response.body().getChoices()[0].getMessage().getContent();
                    if (aiMessage == null || aiMessage.trim().isEmpty()) {
                        aiMessage = "(Respuesta vacía del modelo)";
                    }
                    chatMessages.add(new ChatMessage(aiMessage, false));
                    chatAdapter.notifyDataSetChanged();
                    scrollToBottom();
                } else {
                    String errorBody = null;
                    try { if (response.errorBody() != null) errorBody = response.errorBody().string(); } catch (Exception ignored) {}
                    android.util.Log.e("EpicPop", "Fallo respuesta Groq: code=" + response.code() + " body=" + errorBody);
                    showErrorMessage();
                }
            }

            @Override
            public void onFailure(Call<GroqApiService.GroqResponse> call, Throwable t) {
                hideThinkingAnimation();
                android.util.Log.e("EpicPop", "Error red Groq", t);
                showErrorMessage();
                runOnUiThread(() -> Toast.makeText(AIAssistantActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void showThinkingAnimation() {
        lottieThinking.setVisibility(View.VISIBLE);
        lottieThinking.playAnimation();
        btnSend.setEnabled(false);
    }

    private void hideThinkingAnimation() {
        lottieThinking.setVisibility(View.GONE);
        lottieThinking.cancelAnimation();
        btnSend.setEnabled(true);
    }

    private void showErrorMessage() {
        String errorMessage = "Lo siento, no pude procesar tu mensaje en este momento. Por favor intenta de nuevo más tarde. 🤖";
        chatMessages.add(new ChatMessage(errorMessage, false));
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void scrollToBottom() {
        rvChat.scrollToPosition(chatMessages.size() - 1);
    }

    // Clase para mensajes del chat
    public static class ChatMessage {
        private String message;
        private boolean isUser;
        private long timestamp;

        public ChatMessage(String message, boolean isUser) {
            this.message = message;
            this.isUser = isUser;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public boolean isUser() { return isUser; }
        public long getTimestamp() { return timestamp; }
    }
}
