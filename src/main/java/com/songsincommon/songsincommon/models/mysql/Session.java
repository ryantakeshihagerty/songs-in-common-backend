package com.songsincommon.songsincommon.models.mysql;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Session {
    @Id
    private final String uniqueCode;

    private final String inviterId;

    private String inviteeId;

    public Session(String uniqueCode, String inviterId) {
        this.uniqueCode = uniqueCode;
        this.inviterId = inviterId;
    }

    public void setInviteeId(String inviteeId) {
        this.inviteeId = inviteeId;
    }

    public String getInviterId() {
        return inviterId;
    }

    public String getInviteeId() {
        return inviteeId;
    }
}
