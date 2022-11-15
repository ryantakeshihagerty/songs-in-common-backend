package com.songsincommon.songsincommon.controllers;

import com.songsincommon.songsincommon.services.AuthService;
import com.songsincommon.songsincommon.services.SpotifyService;
import com.songsincommon.songsincommon.services.impl.SessionManager;
import com.songsincommon.songsincommon.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class SongsInCommonController {

    @Value("${application.url.explore}")
    private String exploreUrl;
    @Autowired
    private AuthService authService;
    @Autowired
    private SpotifyService spotifyService;
    @Autowired
    private SessionManager sessionManager;

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @CrossOrigin
    @GetMapping(value = "/login")
    public String loginUser() {
        logger.info("login endpoint called");
        return authService.authorizeUserFor(Constants.getUserTracksScope + " " + Constants.createPlaylistScope);
    }

    @CrossOrigin
    @GetMapping(value = "/callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        logger.info("callback endpoint called");
        String currentUserId = authService.callback(code);
        response.sendRedirect(exploreUrl + "/?userId=" + currentUserId);
    }

    @CrossOrigin
    @GetMapping(value = "/songs/inviter/{userId}")
    public void processInviter(@PathVariable String userId) { // return unique code in response body
        logger.info("Inviter endpoint called for user: {}", userId);

        // Call Spotify Web API and save inviter tracks to DB
        spotifyService.storeSavedSongs(userId);

        // Generate unique code
        sessionManager.generateUniqueCode(userId);

        //response.sendRedirect(shareUrl + "/?code=" + uniqueCode);
    }

    @CrossOrigin
    @GetMapping(value = "/songs/invitee/{userId}")
    public void saveInviteeSongs(@PathVariable String userId, @RequestParam String uniqueCode) {
        logger.info("Invitee endpoint called for user: {}", userId);

        // Save Invitee userId to the key uniqueCode. Now each entry should have uniqueCode, inviter ID, invitee ID
        sessionManager.saveInviteeId(uniqueCode, userId);

        // Call Spotify Web API and save invitee tracks to DB
        spotifyService.storeSavedSongs(userId);

        // Find tracks in common between inviter and invitee. Call Spotify Web API to create playlist.
        spotifyService.createCommonSongsPlaylist(sessionManager.getInviterId(uniqueCode), sessionManager.getInviteeId(uniqueCode));

        //response.sendRedirect(resultsUrl + "/?userId=" + currentUserId);
    }
}
