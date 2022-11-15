package com.songsincommon.songsincommon.services.impl;

import com.songsincommon.songsincommon.services.ClientSecretService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!local")
@Service
public class AmazonSecretsManagerService implements ClientSecretService {
    @Override
    public String getClientSecret() {
        return "mySecret";
    }
}
