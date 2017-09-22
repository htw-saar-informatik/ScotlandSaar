package com.denweisenseel.com.backend.data;

import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.tools.GraphBuilder;
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
    private Geolocation geolocation = new Geolocation();
    private int id;

    private PlayerState playerState = PlayerState.IS_DONE;

    public boolean isOwner() {
        return isOwner;
    }

    public void setMisterX(boolean misterX) {
        isMisterX = misterX;
    }

    public int getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(int targetPosition) {
        this.targetPosition = targetPosition;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

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
        Node n = GraphBuilder.getGraph().get(boardPosition);
        setGeolocation(new Geolocation(n.getLatitude(),n.getLongitude()));
        //TODO make this better @Luca

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

    public void setLocation(Geolocation location) {
        this.geolocation = location;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public enum PlayerState {

        IS_MOVING,IS_DONE, IS_SELECTING;
    }
}
