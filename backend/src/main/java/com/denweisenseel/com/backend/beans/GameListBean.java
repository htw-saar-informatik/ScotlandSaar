package com.denweisenseel.com.backend.beans;

/**
 * Created by denwe on 18.09.2017.
 */

public class GameListBean {

    private String gameName;
    private long gameId;
    private String host;
    private int playerCount;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }


}
