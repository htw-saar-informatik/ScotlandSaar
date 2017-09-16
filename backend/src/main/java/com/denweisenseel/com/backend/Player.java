package com.denweisenseel.com.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by denwe on 16.09.2017.
 */

public class Player {

    private String name;
    private String firebaseToken;
    boolean isOwner = false;
    boolean isMisterX = false;

    private int boardPosition = -1;
    private int targetPosition = -1;

    private PlayerState playerState = PlayerState.IS_DONE;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public void setBoardPosition(int boardPosition) {
        this.boardPosition = boardPosition;
    }

    public void setIsMisterX(boolean isMisterX) {
        this.isMisterX = isMisterX;
    }

    public boolean isMisterX() {
        return isMisterX;
    }

    public int getBoardPosition() {
        return boardPosition;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    enum PlayerState {

        IS_MOVING,IS_DONE, IS_SELECTING;
    }
}
