package com.denweisenseel.com.backend.beans;

import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Player;

/**
 * Created by denwe on 16.09.2017.
 */

public class PlayerBean {

    private String name;
    private Geolocation geolocation;
    private int boardPosition;
    private int selectedBoardPosition;
    private Player.PlayerState playerState;
    private boolean owner;
    private boolean misterX;
    private int id;


    public PlayerBean(Player p) {
        name = p.getName();
        geolocation = p.getGeolocation();
        boardPosition = p.getBoardPosition();
        selectedBoardPosition = p.getBoardPosition();
        playerState = p.getPlayerState();
        owner = p.isOwner();
        misterX = p.isMisterX();
        id = p.getId();
    };


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public int getBoardPosition() {
        return boardPosition;
    }

    public void setBoardPosition(int boardPosition) {
        this.boardPosition = boardPosition;
    }

    public int getSelectedBoardPosition() {
        return selectedBoardPosition;
    }

    public void setSelectedBoardPosition(int selectedBoardPosition) {
        this.selectedBoardPosition = selectedBoardPosition;
    }

    public Player.PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(Player.PlayerState playerState) {
        this.playerState = playerState;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isMisterX() {
        return misterX;
    }

    public void setMisterX(boolean misterX) {
        this.misterX = misterX;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
