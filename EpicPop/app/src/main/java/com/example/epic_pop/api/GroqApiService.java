package com.example.epic_pop.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public interface GroqApiService {

    @POST("chat/completions")
    Call<GroqResponse> generateChatCompletion(
        @Header("Authorization") String authorization,
        @Header("Content-Type") String contentType,
        @Body GroqRequest request
    );

    // Request flexible que admite historial completo de mensajes
    class GroqRequest {
        @SerializedName("model")
        private String model;

        @SerializedName("messages")
        private List<Message> messages;

        @SerializedName("max_tokens")
        private Integer maxTokens;

        @SerializedName("temperature")
        private Double temperature;

        @SerializedName("top_p")
        private Double topP;

        public GroqRequest(List<Message> messages) {
            this(messages, "llama3-8b-8192", 800, 0.7, 1.0);
        }

        public GroqRequest(List<Message> messages, String model, Integer maxTokens, Double temperature, Double topP) {
            this.messages = messages;
            this.model = model == null ? "llama3-8b-8192" : model;
            this.maxTokens = maxTokens == null ? 800 : maxTokens;
            this.temperature = temperature == null ? 0.7 : temperature;
            this.topP = topP == null ? 1.0 : topP;
        }

        // Clase de mensaje reutilizable públicamente
        public static class Message {
            @SerializedName("role")
            private String role;

            @SerializedName("content")
            private String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }

            public String getRole() { return role; }
            public String getContent() { return content; }
        }
    }

    class GroqResponse {
        @SerializedName("choices")
        private Choice[] choices;

        public Choice[] getChoices() { return choices; }

        public static class Choice {
            @SerializedName("message")
            private Message message;

            public Message getMessage() { return message; }

            public static class Message {
                @SerializedName("role")
                private String role;

                @SerializedName("content")
                private String content;

                public String getContent() { return content; }
                public String getRole() { return role; }
            }
        }
    }
}
