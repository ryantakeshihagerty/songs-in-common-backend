package com.songsincommon.songsincommon.services;

import com.songsincommon.songsincommon.models.mongoDb.UserSavedTracks;
import com.songsincommon.songsincommon.models.spotify.AddTracksToPlaylistRequest;
import com.songsincommon.songsincommon.models.spotify.CreatePlaylistRequest;
import com.songsincommon.songsincommon.models.spotify.CreatePlaylistResponse;
import com.songsincommon.songsincommon.models.spotify.TracksResponse;
import com.songsincommon.songsincommon.repository.SavedTracksRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class SpotifyService {

    @Value("${spotify.clientId}")
    private String clientId;
    @Value("${spotify.url.tracks}")
    private String tracksUrl;
    @Value("${spotify.url.createPlaylist}")
    private String createPlaylistUrl;
    @Value("${spotify.url.addToPlaylist}")
    private String addToPlaylistUrl;

    @Autowired
    private ClientSecretService clientSecretService;
    @Autowired
    private AuthService authService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private SavedTracksRepository savedTracksRepository;
    @Autowired
    private RestTemplate restTemplate;

    Logger logger = LoggerFactory.getLogger(SpotifyService.class);

    public void storeSavedSongs(String userId) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(tokenService.getTokenForUser(userId));
        headers.set("Content-Type", "application/json");

        // Set query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(tracksUrl)
                .queryParam("limit", 50)
                .queryParam("market", "US")
                .queryParam("offset", 0);

        logger.info("GET request for endpoint: {}", builder.toUriString());
        logger.info("Headers for request: {}", headers);

        // Execute API call
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<TracksResponse> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                requestEntity,
                TracksResponse.class
        );
        TracksResponse tracksResponse = responseEntity.getBody();
        logger.info("Initial tracks response for user {}: {}", userId, tracksResponse);

        // Create UserSavedTracks obj to add all tracks. Will store this object in MongoDb after iteration done.
        UserSavedTracks userSavedTracks = new UserSavedTracks(userId);

        // Save all tracks from 1st API call to list
        for (TracksResponse.Item item : tracksResponse.getItems()) {
            TracksResponse.Track track = item.getTrack();
            userSavedTracks.addTrack(track);
        }

        // Keep iterating until we get all tracks for this user
        int count = 1;
        while (tracksResponse.getNext() != null) {
            responseEntity = restTemplate.exchange(
                    tracksResponse.getNext(),
                    HttpMethod.GET,
                    requestEntity,
                    TracksResponse.class
            );
            tracksResponse = responseEntity.getBody();

            // Store all tracks from this response for user in DB
            for (TracksResponse.Item item : tracksResponse.getItems()) {
                TracksResponse.Track track = item.getTrack();
                userSavedTracks.addTrack(track);
                logger.info("Adding track: {} to list", track);
            }

            logger.info("{}th tracks response: {}", count, tracksResponse);
            count++;
        }

        // Done iterating, store UserSavedTracks object to MongoDB
        logger.info("Saving to database: {}", userSavedTracks);
        savedTracksRepository.save(userSavedTracks);

    }

    public void createCommonSongsPlaylist(String inviterId, String inviteeId) {
        // DB repo class get all tracks for inviterId
        // DB repo calss get all tracks for invitee Id
        // For songs in common, save track info to new table where unique code is the PK
        //
        // OR
        // just create aggregation of intersection between inviterId and inviteeId
        List<TracksResponse.Track> sharedTracks = getSharedTracks(inviterId, inviteeId);


        // Will use same create playlist request for both inviter and invitee
        CreatePlaylistRequest createPlaylistRequest = new CreatePlaylistRequest();
        createPlaylistRequest.setPlaylistName("Songs in common for " + inviterId + " and " + inviteeId);
        createPlaylistRequest.setPlaylistPublic(true);
        createPlaylistRequest.setPlaylistCollaborative(false);
        createPlaylistRequest.setPlaylistDesc("Generated using songsincommon.io");

        // Create playlist for inviter
        CreatePlaylistResponse inviterPlaylistResponse = createPlaylist(createPlaylistRequest, inviterId);
        // Create playlist for invitee
        CreatePlaylistResponse inviteePlaylistResponse = createPlaylist(createPlaylistRequest, inviteeId);

        // Iterate through shared songs for unique code (loop)
        List<String> trackUris = new ArrayList<>();

        for (TracksResponse.Track track : sharedTracks) {
            trackUris.add(track.getUri());

            // Can only add 100 at a time. Add these 100 and clear list for next set
            if (trackUris.size() == 100) {
                addTracksToPlaylist(inviterId, inviterPlaylistResponse.getId(), trackUris);
                addTracksToPlaylist(inviteeId, inviteePlaylistResponse.getId(), trackUris);
                trackUris.clear();
            }
        }

        // Add remaining tracks (< 100)
        addTracksToPlaylist(inviterId, inviterPlaylistResponse.getId(), trackUris);
        addTracksToPlaylist(inviteeId, inviteePlaylistResponse.getId(), trackUris);

    }

    private CreatePlaylistResponse createPlaylist(CreatePlaylistRequest createPlaylistRequest, String userId) {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(tokenService.getTokenForUser(userId));
        headers.set("Content-Type", "application/json");

        // Execute API call
        HttpEntity<CreatePlaylistRequest> requestEntity = new HttpEntity<>(createPlaylistRequest, headers);
        ResponseEntity<CreatePlaylistResponse> responseEntity = restTemplate.postForEntity(
                createPlaylistUrl.replace("user_id", userId),
                requestEntity,
                CreatePlaylistResponse.class
        );
        return responseEntity.getBody();
    }

    private void addTracksToPlaylist(String userId, String playlistId, List<String> trackUris) {
        if (trackUris.size() > 100) {
            logger.error("Error for user: {}. Spotify Web API does not support adding more than 100 tracks at once", userId);
        }

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        headers.setBearerAuth(tokenService.getTokenForUser(userId));
        headers.set("Content-Type", "application/json");

        // Set request body
        AddTracksToPlaylistRequest addTracksToPlaylistRequest = new AddTracksToPlaylistRequest();
        addTracksToPlaylistRequest.setTrackUris(trackUris);

        // Execute API call
        HttpEntity<AddTracksToPlaylistRequest> requestEntity = new HttpEntity<>(addTracksToPlaylistRequest, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                addToPlaylistUrl.replace("playlist_id", playlistId),
                requestEntity,
                String.class
        );

    }

    private List<TracksResponse.Track> getSharedTracks(String inviterId, String inviteeId) {
        // use setintersection for mongo?
        return null;
    }
}
