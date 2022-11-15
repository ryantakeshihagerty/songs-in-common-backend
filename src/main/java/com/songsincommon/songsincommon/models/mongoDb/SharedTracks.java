package com.songsincommon.songsincommon.models.mongoDb;

import com.songsincommon.songsincommon.models.spotify.TracksResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("shared-tracks")
public class SharedTracks {
    @Id
    private String uniqueSessionCode;

    List<TracksResponse.Track> tracks;

    public SharedTracks(String uniqueSessionCode, List<TracksResponse.Track> tracks) {
        this.uniqueSessionCode = uniqueSessionCode;
        this.tracks = tracks;
    }

    public String getUniqueSessionCode() {
        return uniqueSessionCode;
    }

    public void setUniqueSessionCode(String uniqueSessionCode) {
        this.uniqueSessionCode = uniqueSessionCode;
    }

    public List<TracksResponse.Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<TracksResponse.Track> tracks) {
        this.tracks = tracks;
    }
}
