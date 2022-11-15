package com.songsincommon.songsincommon.models.spotify;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatePlaylistRequest {
    private String playlistName;
    private boolean playlistPublic;
    private boolean playlistCollaborative;
    private String playlistDesc;

    @JsonProperty("name")
    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    @JsonProperty("public")
    public boolean isPlaylistPublic() {
        return playlistPublic;
    }

    public void setPlaylistPublic(boolean playlistPublic) {
        this.playlistPublic = playlistPublic;
    }

    @JsonProperty("collaborative")
    public boolean isPlaylistCollaborative() {
        return playlistCollaborative;
    }

    public void setPlaylistCollaborative(boolean playlistCollaborative) {
        this.playlistCollaborative = playlistCollaborative;
    }

    @JsonProperty("description")
    public String getPlaylistDesc() {
        return playlistDesc;
    }

    public void setPlaylistDesc(String playlistDesc) {
        this.playlistDesc = playlistDesc;
    }
}
