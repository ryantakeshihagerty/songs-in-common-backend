package com.songsincommon.songsincommon.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AddTracksToPlaylistRequest {
    private List<String> trackUris;

    @JsonProperty("uris")
    public List<String> getTrackUris() {
        return trackUris;
    }

    public void setTrackUris(List<String> trackUris) {
        this.trackUris = trackUris;
    }
}
