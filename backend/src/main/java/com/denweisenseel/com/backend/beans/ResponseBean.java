package com.denweisenseel.com.backend.beans;

/**
 * The object model for the data we are sending through endpoints
 */
public class ResponseBean {

    private boolean success;
    private long gameId;


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
}