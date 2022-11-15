package com.songsincommon.songsincommon.services.impl;

import com.songsincommon.songsincommon.services.ClientSecretService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("local")
@Service
public class LocalSecretService implements ClientSecretService {

    @Value("${spotify.clientSecret}")
    private String clientSecret;
    @Override
    public String getClientSecret() {
        return clientSecret;
    }
}
