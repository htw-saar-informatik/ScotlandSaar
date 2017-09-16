package com.denweisenseel.com.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;

/**
 * Created by denwe on 16.09.2017.
 */
@Entity
public class GameBoard {

    @Id Long id;

    ArrayList<Player> playerList;

    //Describing vars, for the GameList
    private String  gameName;
    private String  creatorName;

    //HelperVars for readability
    private boolean X_TURN = false;
    private boolean PLAYER_TURN = true;

    //GameVars
    private boolean gameRunning = false;
    private boolean gameWon = false;
    private boolean xRevealed = false;

    private int     turnCounter = 0;
    private boolean turnState = X_TURN;


    public GameBoard() {
        playerList = new ArrayList<Player>();
    }


    public int addPlayer(String firebaseToken, String name) {
        Player p = new Player();
        p.setName(name);
        p.setFirebaseToken(firebaseToken);
        playerList.add(p);

        int returnValue = playerList.indexOf(p);

        return playerList.indexOf(p);
    }




    public ArrayList<Player> getPlayerList() {
        return playerList;
    }
}
