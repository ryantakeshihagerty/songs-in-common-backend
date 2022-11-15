package com.songsincommon.songsincommon.services;

import com.songsincommon.songsincommon.models.TokenResponse;
import com.songsincommon.songsincommon.services.ClientSecretService;
import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRefreshRequest;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {

    @Value("${spotify.clientId}")
    String clientId;

    @Autowired
    private ClientSecretService clientSecretService;

    private static Map<String, TokenResponse> userTokens = new HashMap<>();

    public String getTokenForUser(String userId) {
        if (!tokenIsValid(userTokens.get(userId))) { // Token not valid, refresh
            refreshToken(userId);
        }
        return userTokens.get(userId).getAccessToken();
    }

    public void storeTokenForUser(String userId, TokenResponse tokenResponse) {
        userTokens.put(userId, tokenResponse);
    }

    private boolean tokenIsValid(TokenResponse tokenResponse) {
        long elapsedTimeInSeconds = ChronoUnit.SECONDS.between(ZonedDateTime.now(), tokenResponse.getTokenFetchTime());

        if (elapsedTimeInSeconds >= tokenResponse.getExpiresIn()) {
            return false;
        }

        return true;
    }

    private void refreshToken(String userId) {
        String refreshToken = userTokens.get(userId).getRefreshToken();

        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setClientId(clientId)
                .setClientSecret(clientSecretService.getClientSecret())
                .setRefreshToken(refreshToken)
                .build();

        AuthorizationCodeRefreshRequest authorizationCodeRefreshRequest = spotifyApi.authorizationCodeRefresh().build();

        try {
            AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRefreshRequest.execute();

            // Grab old token info
            TokenResponse tokenResponse = userTokens.get(userId);

            // Update with new values
            tokenResponse.setAccessToken(authorizationCodeCredentials.getAccessToken());
            tokenResponse.setTokenType(authorizationCodeCredentials.getTokenType());
            tokenResponse.setScope(authorizationCodeCredentials.getScope());
            tokenResponse.setExpiresIn(authorizationCodeCredentials.getExpiresIn());
            tokenResponse.setTokenFetchTime(ZonedDateTime.now());

            // Update token response for this user
            userTokens.put(userId, tokenResponse);

        } catch (IOException | ParseException | SpotifyWebApiException e) {
            throw new RuntimeException(e);
        }
    }
}
