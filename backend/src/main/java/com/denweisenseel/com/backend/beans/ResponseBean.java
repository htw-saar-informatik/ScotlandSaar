package com.denweisenseel.com.backend.beans;

import com.denweisenseel.com.backend.data.Player;

import java.util.ArrayList;

/**
 * The object model for the data we are sending through endpoints
 */
public class ResponseBean {

    private boolean success;
    private long gameId;
    private int playerId;
    private ArrayList<Player> playerInLobby;

    public void setPlayerInLobby(ArrayList<Player> playerInLobby){
        this.playerInLobby = playerInLobby;
    }

    public ArrayList<Player> getPlayerInLobby(){
        return this.playerInLobby;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }
}