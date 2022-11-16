package com.songsincommon.songsincommon.services;

import com.songsincommon.songsincommon.models.mysql.Session;
import com.songsincommon.songsincommon.repository.SessionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class SessionManagerTest {

    @InjectMocks
    private SessionManager sessionManager;

    @Mock
    private SessionRepository sessionRepository;

    @Test
    public void generateUniqueCodeTest() {
        String inviterId = "spotifyuser";

        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.empty());
        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(new Session("", inviterId));

        assertNotNull(sessionManager.generateUniqueCode(inviterId));
    }

    @Test
    public void generateUniqueCodeTwiceTest() {
        String inviterId = "spotifyuser";
        Session existingSession = new Session("ABCD1234", "anotheruser");

        Mockito.when(sessionRepository.findById(anyString())).thenReturn(Optional.of(existingSession), Optional.empty());
        Mockito.when(sessionRepository.save(any(Session.class))).thenReturn(new Session("", inviterId));

        assertNotNull(sessionManager.generateUniqueCode(inviterId));
    }
}