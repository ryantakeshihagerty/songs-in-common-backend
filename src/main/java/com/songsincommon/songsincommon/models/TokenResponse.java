package com.songsincommon.songsincommon.models;

import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;

import java.time.ZonedDateTime;

public class TokenResponse {
    private String accessToken;
    private String tokenType;
    private String scope;
    private int expiresIn;
    private String refreshToken;

    private ZonedDateTime tokenFetchTime;

    public TokenResponse(String accessToken, String tokenType, String scope, int expiresIn, String refreshToken, ZonedDateTime tokenFetchTime) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.tokenFetchTime = tokenFetchTime;
    }

    public TokenResponse(AuthorizationCodeCredentials authorizationCodeCredentials, ZonedDateTime tokenFetchTime) {
        this.accessToken = authorizationCodeCredentials.getAccessToken();
        this.tokenType = authorizationCodeCredentials.getTokenType();
        this.scope = authorizationCodeCredentials.getScope();
        this.expiresIn = authorizationCodeCredentials.getExpiresIn();
        this.refreshToken = authorizationCodeCredentials.getRefreshToken();
        this.tokenFetchTime = tokenFetchTime;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public ZonedDateTime getTokenFetchTime() {
        return tokenFetchTime;
    }

    public void setTokenFetchTime(ZonedDateTime tokenFetchTime) {
        this.tokenFetchTime = tokenFetchTime;
    }
}
