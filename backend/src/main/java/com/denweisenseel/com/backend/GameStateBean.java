package com.denweisenseel.com.backend;

import java.util.ArrayList;

/**
 * Created by denwe on 16.09.2017.
 */

public class GameStateBean {

    ArrayList<Player> playerList;

    private byte    gameState;
    private boolean gameWon;

    private int     turnCounter;
    private boolean turnState;

    public GameStateBean() {
        playerList = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        playerList.add(p);
    }

    public void setGameState(byte gameState) {
        this.gameState = gameState;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public void setTurnCounter(int turnCounter) {
        this.turnCounter = turnCounter;
    }

    public void setTurnState(boolean turnState) {
        this.turnState = turnState;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public byte getGameState() {
        return gameState;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public int getTurnCounter() {
        return turnCounter;
    }

    public boolean isTurnState() {
        return turnState;
    }
}
