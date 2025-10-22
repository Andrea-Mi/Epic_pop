package com.example.epic_pop.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import com.google.gson.annotations.SerializedName;

public interface DeezerApiService {

    @GET("album/{albumId}")
    Call<DeezerAlbum> getAlbum(@Path("albumId") String albumId);

    @GET("track/{trackId}")
    Call<DeezerTrack> getTrack(@Path("trackId") String trackId);

    @GET("search/track?q={query}")
    Call<DeezerSearchResponse> searchTracks(@Path("query") String query);

    class DeezerAlbum {
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("cover_medium")
        private String coverMedium;

        @SerializedName("tracks")
        private DeezerTrackData tracks;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getCoverMedium() { return coverMedium; }
        public DeezerTrackData getTracks() { return tracks; }
    }

    class DeezerTrackData {
        @SerializedName("data")
        private DeezerTrack[] data;

        public DeezerTrack[] getData() { return data; }
    }

    class DeezerTrack {
        @SerializedName("id")
        private String id;

        @SerializedName("title")
        private String title;

        @SerializedName("artist")
        private DeezerArtist artist;

        @SerializedName("duration")
        private int duration;

        @SerializedName("preview")
        private String preview;

        public String getId() { return id; }
        public String getTitle() { return title; }
        public DeezerArtist getArtist() { return artist; }
        public int getDuration() { return duration; }
        public String getPreview() { return preview; }
    }

    class DeezerArtist {
        @SerializedName("name")
        private String name;

        public String getName() { return name; }
    }

    class DeezerSearchResponse {
        @SerializedName("data")
        private DeezerTrack[] data;

        public DeezerTrack[] getData() { return data; }
    }
}
