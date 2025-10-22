package com.example.epic_pop.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.Log;
import com.example.epic_pop.api.DeezerApiService;
import com.example.epic_pop.utils.ApiKeyManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class MusicManager {

    private static final String TAG = "MusicManager";
    private static final String DEEZER_BASE_URL = "https://api.deezer.com/";

    private Context context;
    private MediaPlayer mediaPlayer;
    private DeezerApiService deezerService;
    private ApiKeyManager apiKeyManager;
    private boolean isPlaying = false;

    // Listener para eventos de música
    public interface MusicEventListener {
        void onMusicStarted(String trackTitle);
        void onMusicStopped();
        void onMusicError(String error);
    }

    private MusicEventListener musicEventListener;

    public MusicManager(Context context) {
        this.context = context;
        this.apiKeyManager = new ApiKeyManager(context);
        initDeezerService();
        initMediaPlayer();
    }

    private void initDeezerService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DEEZER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        deezerService = retrofit.create(DeezerApiService.class);
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnCompletionListener(mp -> {
            isPlaying = false;
            if (musicEventListener != null) {
                musicEventListener.onMusicStopped();
            }
        });

        mediaPlayer.setOnErrorListener((mp, what, extra) -> {
            Log.e(TAG, "MediaPlayer error: " + what + ", " + extra);
            if (musicEventListener != null) {
                musicEventListener.onMusicError("Error de reproducción");
            }
            return true;
        });
    }

    /**
     * Reproduce música motivacional al completar un reto
     */
    public void playMotivationalMusic() {
        loadAlbumAndPlay("302127"); // Album ID de Deezer especificado
    }

    /**
     * Reproduce música según el estado de ánimo
     */
    public void playMoodMusic(String mood) {
        switch (mood.toLowerCase()) {
            case "motivacion":
            case "energia":
                searchAndPlay("motivational workout music");
                break;
            case "relajacion":
            case "calma":
                searchAndPlay("relaxing ambient music");
                break;
            case "estudio":
                searchAndPlay("study focus music");
                break;
            default:
                playMotivationalMusic();
                break;
        }
    }

    /**
     * Reproduce música por categoría de reto
     */
    public void playMusicForCategory(String category) {
        switch (category) {
            case "Deportes":
                searchAndPlay("workout gym music");
                break;
            case "Estudio":
                searchAndPlay("study concentration music");
                break;
            case "Ecología":
                searchAndPlay("nature sounds ambient");
                break;
            case "Salud":
                searchAndPlay("meditation wellness music");
                break;
            case "Creatividad":
                searchAndPlay("creative inspiration music");
                break;
            default:
                playMotivationalMusic();
                break;
        }
    }

    private void loadAlbumAndPlay(String albumId) {
        Call<DeezerApiService.DeezerAlbum> call = deezerService.getAlbum(albumId);

        call.enqueue(new Callback<DeezerApiService.DeezerAlbum>() {
            @Override
            public void onResponse(Call<DeezerApiService.DeezerAlbum> call,
                                   Response<DeezerApiService.DeezerAlbum> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeezerApiService.DeezerAlbum album = response.body();
                    if (album.getTracks() != null && album.getTracks().getData().length > 0) {
                        // Reproducir primera canción del álbum
                        DeezerApiService.DeezerTrack track = album.getTracks().getData()[0];
                        playTrack(track);
                    }
                } else {
                    Log.e(TAG, "Error loading album: " + response.code());
                    if (musicEventListener != null) {
                        musicEventListener.onMusicError("No se pudo cargar el álbum");
                    }
                }
            }

            @Override
            public void onFailure(Call<DeezerApiService.DeezerAlbum> call, Throwable t) {
                Log.e(TAG, "Network error loading album", t);
                if (musicEventListener != null) {
                    musicEventListener.onMusicError("Error de conexión");
                }
            }
        });
    }

    private void searchAndPlay(String query) {
        Call<DeezerApiService.DeezerSearchResponse> call = deezerService.searchTracks(query);

        call.enqueue(new Callback<DeezerApiService.DeezerSearchResponse>() {
            @Override
            public void onResponse(Call<DeezerApiService.DeezerSearchResponse> call,
                                   Response<DeezerApiService.DeezerSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DeezerApiService.DeezerSearchResponse searchResponse = response.body();
                    if (searchResponse.getData() != null && searchResponse.getData().length > 0) {
                        // Reproducir primer resultado
                        DeezerApiService.DeezerTrack track = searchResponse.getData()[0];
                        playTrack(track);
                    }
                }
            }

            @Override
            public void onFailure(Call<DeezerApiService.DeezerSearchResponse> call, Throwable t) {
                Log.e(TAG, "Network error searching tracks", t);
                if (musicEventListener != null) {
                    musicEventListener.onMusicError("Error de búsqueda");
                }
            }
        });
    }

    private void playTrack(DeezerApiService.DeezerTrack track) {
        if (track.getPreview() != null && !track.getPreview().isEmpty()) {
            new PlayTrackTask(track).execute();
        } else {
            Log.w(TAG, "No preview available for track: " + track.getTitle());
            if (musicEventListener != null) {
                musicEventListener.onMusicError("Vista previa no disponible");
            }
        }
    }

    private class PlayTrackTask extends AsyncTask<Void, Void, Boolean> {
        private DeezerApiService.DeezerTrack track;

        public PlayTrackTask(DeezerApiService.DeezerTrack track) {
            this.track = track;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.setDataSource(track.getPreview());
                mediaPlayer.prepare();
                mediaPlayer.start();
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error playing track", e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                isPlaying = true;
                if (musicEventListener != null) {
                    String title = track.getTitle() + " - " + track.getArtist().getName();
                    musicEventListener.onMusicStarted(title);
                }
            } else {
                if (musicEventListener != null) {
                    musicEventListener.onMusicError("Error al reproducir");
                }
            }
        }
    }

    // Control de reproducción
    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
        }
    }

    public void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            isPlaying = true;
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            isPlaying = false;
            if (musicEventListener != null) {
                musicEventListener.onMusicStopped();
            }
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setMusicEventListener(MusicEventListener listener) {
        this.musicEventListener = listener;
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
