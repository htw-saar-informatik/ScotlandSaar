package com.denweisenseel.com.backend;

import com.denweisenseel.com.backend.beans.GameStateBean;
import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Node;
import com.denweisenseel.com.backend.data.Player;
import com.denweisenseel.com.backend.exceptions.PlayerNotFoundException;
import com.denweisenseel.com.backend.tools.GraphBuilder;
import com.denweisenseel.com.backend.tools.PushNotificationBuilder;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by denwe on 16.09.2017.
 */
@Entity
public class GameBoard {

    @Id Long id;

    ArrayList<Player> playerList;
    Random random;

    //Describing vars, for the GameList
    private String  gameName;
    private String  creatorName;

    //HelperVars for readability
    private boolean X_TURN = false;
    private boolean PLAYER_TURN = true;

    private byte GAMESTATE_SETUP = 0x01;
    private byte GAMESTATE_RUNNING = 0x02;
    private byte GAMESTATE_OVER = 0x03;

    //GameVars
    private byte    gameState = GAMESTATE_SETUP;
    private boolean gameWon = false;
    private boolean xRevealed = false;
    private int     misterXId;

    private int     turnCounter = 0;
    private boolean turnState = X_TURN;
    private boolean gpsEnabled = false;


    public GameBoard() {
        playerList = new ArrayList<Player>();
        random = new Random("Dennis".hashCode());
    }

    public boolean createGame(String firebaseToken, String playerName, String gameName) {
        Player p = new Player();
        p.setOwner(true);
        p.setName(playerName);
        p.setFirebaseToken(firebaseToken);

        addPlayer(p);

        return true;
    }

    public boolean joinGame(String firebaseToken, String playerName) {
        if(gameState != GAMESTATE_SETUP) return false;
        Player p = new Player();
        p.setFirebaseToken(firebaseToken);
        p.setName(playerName);
        int id = addPlayer(p);

        if(id != -1) {
            notifyPlayerJoinedLobby(p);
            return true;
        }
        return false;
    }

    public boolean startGame() {
        if(gameState != GAMESTATE_SETUP) return false;

        //Setup game:

        //ASSIGN Mister X
        int misterX = random.nextInt(playerList.size());

        misterXId = misterX;
        playerList.get(misterX).setIsMisterX(true);

        //PLACE players

        assignRandomPositions();
        //SEND game data
        notifyPlayerGameStart();

        startMisterXTurn();


        return true;

    }




    public boolean makeMove(String fireBaseToken, int targetNodeId) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(fireBaseToken);

        if(p.getPlayerState() != Player.PlayerState.IS_SELECTING) {
            return false; // Du hast schon ausgewählt!
        }

        if(canReach(p,targetNodeId) && isFree(targetNodeId)) {
            if(gpsEnabled) {
                p.setPlayerState(Player.PlayerState.IS_MOVING);
                p.setBoardPosition(targetNodeId);
                notifyPlayerMoving(p);
            } else {
                p.setPlayerState(Player.PlayerState.IS_DONE);
                notifyPlayerDone(p);
            }

            advanceGameState();
            return true;
        }
        return false;
    }

    public boolean updatePosition(String firebaseToken, Geolocation location) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(firebaseToken);
        p.setLocation(location);

        if(p.getPlayerState() == Player.PlayerState.IS_MOVING) {
            if (GraphBuilder.getGraph().get(p.getBoardPosition()).getLocation().distanceBetweenGeolocationInMetres(location) < 20) {
                p.setPlayerState(Player.PlayerState.IS_DONE);
                return true;
            }
        }

        return false;
    }

    private void notifyPlayerDone(Player p) {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_POSITION_REACHED)
                    .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_SELECT_ID, playerList.indexOf(p))
                    .push();
        }
    }


    private boolean isFree(int targetNodeId) {
        boolean free = true;
        for(Player p : playerList) {
            //Wenn ein Spieler, der nicht MisterX ist, auf der Position steht ist diese blockiert.
            if(p.getBoardPosition() == targetNodeId && !p.isMisterX()) {
                if(p.getPlayerState() != Player.PlayerState.IS_SELECTING) {
                    free =false;
                }
            }
        }
        return free;
    }

    private boolean canReach(Player p, int targetNodeId) {
        return GraphBuilder.getGraph().get(p.getBoardPosition()).hasNeighbour(targetNodeId);
    }

    private void advanceGameState() {
        if(turnState == X_TURN) {
            if(playerList.get(misterXId).getPlayerState() == Player.PlayerState.IS_DONE) {
                startPlayerTurn();
            }
        } else {
            if(allPlayersAreDone()) {
                if(gameWon) {
                    notifyPlayerGameWon();
                } else if(turnCounter >= 12) {
                    notifyPlayerGameLost();
                }
                startMisterXTurn();
            }
        }
    }


    private void notifyPlayerMoving(Player p) {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_POSITION_SELECTED)
                    .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_SELECT_POSITION, p.getBoardPosition())
                    .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_SELECT_ID, playerList.indexOf(p))
                    .push();
        }
    }

    private void notifyPlayerGameLost() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_LOST)
                    .push();
        }
    }

    private void notifyPlayerGameWon() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_WON)
                    .push();
        }
    }

    private boolean allPlayersAreDone() {
        for(Player p : playerList) {
            if(p.getPlayerState() != Player.PlayerState.IS_DONE) return false;
        }
        return true;
    }

    private void startPlayerTurn() {
        turnState = PLAYER_TURN;
        notifyPlayerTurnStart();
    }

    private void notifyPlayerTurnStart() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_TURN_START_PLAYER)
                    .push();
        }
    }

    private void startMisterXTurn() {
        playerList.get(misterXId).setPlayerState(Player.PlayerState.IS_SELECTING);
        turnState = X_TURN;

        turnCounter++;

        if(turnCounter == 2 || turnCounter == 5 || turnCounter == 8) {
            broadcastMisterXPosition();
        }

        notifyMisterXStartTurn();
    }

    private void broadcastMisterXPosition() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                        .addRecipient(q.getFirebaseToken())
                        .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_REVEAL_X)
                        .addDataAttribute(PushNotificationBuilder.DataType.MISTER_X_POSITION, playerList.get(misterXId).getBoardPosition())
                        .push();
        }
    }


    public void assignRandomPositions() {
        ArrayList<Node> graph = GraphBuilder.getGraph();

        int size = graph.size();
        Player misterX = playerList.get(misterXId);
        int randomNumber = random.nextInt(size);
        misterX.setBoardPosition(graph.get(randomNumber).getId());

        for(Player p : playerList) {
            if(!p.isMisterX()) {
                assignPlayerRandomPosition(p,graph);
            }
        }
    }


    private void assignPlayerRandomPosition(Player p,ArrayList<Node> g) {
        int misterXPositionId = playerList.get(misterXId).getBoardPosition();
        int randomNodeId = random.nextInt(g.size());
        if(g.get(randomNodeId).hasNeighbour(misterXPositionId) || hasPlayerOnPosition(randomNodeId)) {
            assignPlayerRandomPosition(p,g);
        } else {
            p.setBoardPosition(g.get(randomNodeId).getId());
        }
    }

    private boolean hasPlayerOnPosition(int randomNodeId) {
        for(Player p : playerList) {
            if(p.getBoardPosition() == randomNodeId) {
                return true;
            }
        }
        return false;
    }


    public int addPlayer(Player p) {
        playerList.add(p);
        int returnValue = playerList.indexOf(p);
        return returnValue;
    }

    public boolean removePlayer(String firebaseToken, String name) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(firebaseToken);
        if(playerList.remove(p)) {
            notifyPlayerLeftLobby(p);
        }
        return playerList.remove(p);
    }

    private void notifyPlayerGameStart() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.LOBBY_GAME_START)
                    .push();

        }
    }


    private void notifyPlayerLeftLobby(Player p) {
        for(Player q : playerList) {
            if(p.getFirebaseToken() != q.getFirebaseToken()) {
                new PushNotificationBuilder()
                        .addRecipient(q.getFirebaseToken())
                        .setNotificationType(PushNotificationBuilder.PushNotificationType.LOBBY_PLAYER_LEAVE)
                        .push();
            }
        }
    }


    private void notifyPlayerJoinedLobby(Player p) {
        for(Player q : playerList) {
            if(p.getFirebaseToken() != q.getFirebaseToken()) {
                new PushNotificationBuilder()
                        .addRecipient(q.getFirebaseToken())
                        .setNotificationType(PushNotificationBuilder.PushNotificationType.LOBBY_PLAYER_JOIN)
                        .push();
            }
        }
    }

    private void notifyMisterXStartTurn() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_TURN_START_X)
                    .push();
        }
    }

    private Player getPlayerByFirebaseToken(String firebaseToken) throws PlayerNotFoundException {
        for(Player p : playerList) {
            if(p.getFirebaseToken().equals(firebaseToken)) return p;
        }
        throw new PlayerNotFoundException("Player with fireBaseToken"+ firebaseToken +" was not found");
    }

    public GameStateBean getGameState(boolean misterX) {
        GameStateBean gsb = new GameStateBean();

        //Add players!

        for(Player p : playerList) {
            if((p.isMisterX() && misterX) || p.isMisterX() && xRevealed) {
                gsb.addPlayer(p);
            } else {
                if(!p.isMisterX()) {
                    gsb.addPlayer(p);
                }
            }
        }

        gsb.setGameState(gameState);
        gsb.setGameWon(gameWon);
        gsb.setTurnCounter(turnCounter);
        gsb.setTurnState(turnState);

        return gsb;
    }


    public ArrayList<Player> getPlayerList() {
        return playerList;
    }
}
