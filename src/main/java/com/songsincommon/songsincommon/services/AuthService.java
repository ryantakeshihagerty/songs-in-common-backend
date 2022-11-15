package com.songsincommon.songsincommon.services;

import com.songsincommon.songsincommon.models.TokenResponse;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeUriRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;

@Service
public class AuthService {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Value("${spotify.clientId}")
    String clientId;
    @Value("${application.url.redirect}")
    String redirectUri;

    @Autowired
    private ClientSecretService clientSecretService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private RestTemplate restTemplate;
    private SpotifyApi spotifyApi;


    @PostConstruct
    public void createSpotifyApi() {
        spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecretService.getClientSecret())
                .setRedirectUri(SpotifyHttpManager.makeUri(redirectUri))
                .build();
        logger.info("Spotify API object created");
    }

    public String authorizeUserFor(String scope) {
        AuthorizationCodeUriRequest authorizationCodeUriRequest = spotifyApi.authorizationCodeUri()
                .scope(scope)
                .show_dialog(true)
                .build();

        final URI uri = authorizationCodeUriRequest.execute();
        return uri.toString();
    }

    public String callback(String code) {
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(code).build();

        String currentUserId;

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            currentUserId = storeTokenForUser(authorizationCodeCredentials);
        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }

        return currentUserId;
    }

    private String storeTokenForUser(AuthorizationCodeCredentials authorizationCodeCredentials) {

        // call GET current userinfo
        SpotifyApi spotifyApi2 = new SpotifyApi.Builder()
                .setAccessToken(authorizationCodeCredentials.getAccessToken())
                .build();

        GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi2.getCurrentUsersProfile().build();

        User currentUser;

        try {
            // Call Spotify Web API to get current User ID
            currentUser = getCurrentUsersProfileRequest.execute();
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            throw new RuntimeException(e);
        }

        // Store token info for this current user
        logger.info("Storing token for user: {}", currentUser.getId());
        TokenResponse tokenResponse = new TokenResponse(
                authorizationCodeCredentials.getAccessToken(),
                authorizationCodeCredentials.getTokenType(),
                authorizationCodeCredentials.getScope(),
                authorizationCodeCredentials.getExpiresIn(),
                authorizationCodeCredentials.getRefreshToken(),
                ZonedDateTime.now()
                );

        tokenService.storeTokenForUser(currentUser.getId(), tokenResponse);

        return currentUser.getId();
    }

}
