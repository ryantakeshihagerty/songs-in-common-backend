package com.songsincommon.songsincommon.models.mongoDb;

import com.songsincommon.songsincommon.models.spotify.TracksResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("saved-tracks")  // Name of MongoDB collection
public class UserSavedTracks {

    @Id
    private String userId;

    private List<TracksResponse.Track> tracks;

    public UserSavedTracks(String userId) {
        super();
        this.userId = userId;
    }

    public void addTrack(TracksResponse.Track track) {
        this.tracks.add(track);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<TracksResponse.Track> getTracks() {
        return tracks;
    }

    public void setTracks(List<TracksResponse.Track> tracks) {
        this.tracks = tracks;
    }
}
