package com.songsincommon.songsincommon.services;

import com.songsincommon.songsincommon.models.mysql.Session;
import com.songsincommon.songsincommon.repository.SessionRepository;
import com.songsincommon.songsincommon.util.UniqueCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SessionManager {

    @Autowired
    SessionRepository sessionRepository;

    public String generateUniqueCode(String inviterId) {
        String uniqueCode = UniqueCodeGenerator.generateUniqueCode(8);

        // Check if this code is in use already. Generate a new one until it is unique.
        while (sessionRepository.findById(uniqueCode).isPresent()) {
            uniqueCode = UniqueCodeGenerator.generateUniqueCode(8);
        }

        // Save inviter ID to this unique code
        Session session = new Session(uniqueCode, inviterId);
        sessionRepository.save(session);

        return uniqueCode;
    }

    public void saveInviteeId(String uniqueCode, String userId) {
        Optional<Session> session = sessionRepository.findById(uniqueCode);
        session.get().setInviteeId(userId);
        sessionRepository.save(session.get());
    }

    public String getInviterId(String uniqueCode) {
        Optional<Session> session = sessionRepository.findById(uniqueCode);
        return session.get().getInviterId();
    }

    public String getInviteeId(String uniqueCode) {
        Optional<Session> session = sessionRepository.findById(uniqueCode);
        return session.get().getInviteeId();
    }
}
