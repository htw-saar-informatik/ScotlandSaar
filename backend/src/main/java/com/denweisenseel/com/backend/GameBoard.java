package com.denweisenseel.com.backend;

import com.denweisenseel.com.backend.beans.GameStateBean;
import com.denweisenseel.com.backend.beans.MakeMoveResponseBean;
import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Node;
import com.denweisenseel.com.backend.data.Player;
import com.denweisenseel.com.backend.exceptions.PlayerNotFoundException;
import com.denweisenseel.com.backend.tools.GraphBuilder;
import com.denweisenseel.com.backend.tools.PushNotificationBuilder;
import com.google.gson.Gson;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

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

    public static final byte GAMESTATE_SETUP = 0x01;
    public static final byte GAMESTATE_RUNNING = 0x02;
    public static final byte GAMESTATE_OVER = 0x03;

    //GameVars
    private byte    gameState = GAMESTATE_SETUP;
    private boolean gameWon = false;
    private boolean xRevealed = false;
    private int     misterXId;

    private int     turnCounter = 0;
    private boolean turnState = X_TURN;
    private boolean gpsEnabled = false;
    private int idCount = 0;


    public GameBoard() {
        playerList = new ArrayList<Player>();

    }

    public int createGame(String firebaseToken, String playerName, String gameName) {
        Player p = new Player();
        p.setOwner(true);
        p.setName(playerName);
        p.setFirebaseToken(firebaseToken);
        p.setId(idCount);
        idCount++;

        this.gameName = gameName;
        this.creatorName = playerName;

        addPlayer(p);

        return p.getId();
    }

    public int joinGame(String firebaseToken, String playerName) {
        if(gameState != GAMESTATE_SETUP) return -1;
        Player p = new Player();
        p.setFirebaseToken(firebaseToken);
        p.setName(playerName);
        p.setId(idCount);
        idCount++;
        int id = addPlayer(p);

        if(id != -1) {
            notifyPlayerJoinedLobby(p);

            return p.getId();
        }
        return -1;
    }

    public boolean startGame(String firebaseToken) {

        for(Player p : playerList) {
            if(p.isOwner()) {
                if(!p.getFirebaseToken().equals(firebaseToken)) {
                    return false;
                }
            }
        }

        if(gameState != GAMESTATE_SETUP) return false;

        //Setup game:

        gameState = GAMESTATE_RUNNING;

        //ASSIGN Mister X
        int misterX = new Random("Dennis".hashCode()).nextInt(playerList.size());

        misterXId = misterX;
        playerList.get(misterX).setIsMisterX(true);

        //PLACE players
        assignRandomPositions();
        //SEND game data
        notifyPlayerGameStart();


        startMisterXTurn();


        return true;

    }



    public MakeMoveResponseBean makeMove(String fireBaseToken, int targetNodeId) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(fireBaseToken);


        System.out.println("Request von " +p.getName() + "will zu " + targetNodeId);
        MakeMoveResponseBean responseBean = new MakeMoveResponseBean();


        if(p.getPlayerState() == Player.PlayerState.IS_MOVING) {
            responseBean.setSuccess(false);

            System.out.println("War aber schon am bewegen!" + p.getPlayerState());
            responseBean.setData("You've already selected. Move to your point.");
            return responseBean; // Du hast schon ausgewählt!
        }

        if(p.getPlayerState() == Player.PlayerState.IS_DONE) {
            responseBean.setSuccess(false);
            System.out.println("War aber schon fertig!" + p.getPlayerState());
            responseBean.setData("You're already done.");
            return responseBean; // Du hast schon ausgewählt!
        }

        if(canReach(p,targetNodeId) && isFree(targetNodeId)) {
            System.out.println(targetNodeId + " ist frei und erreichbar.");
            if(gpsEnabled) {
                p.setPlayerState(Player.PlayerState.IS_MOVING);
                p.setBoardPosition(targetNodeId);
                notifyPlayerMoving(p);
            } else {
                p.setPlayerState(Player.PlayerState.IS_DONE);
                p.setBoardPosition(targetNodeId);
                notifyPlayerDone(p);
                advanceGameState();
            }

            responseBean.setSuccess(true);
            responseBean.setData("Alright, let's move to your position.");
            responseBean.setPositionId(targetNodeId);
            return responseBean;
        }
        responseBean.setSuccess(false);
        responseBean.setData("CanReach:" +canReach(p,targetNodeId) + " IsFree:" + isFree(targetNodeId));
        return responseBean;
    }

    public boolean updatePosition(String firebaseToken, Geolocation location) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(firebaseToken);
        p.setLocation(location);

        if(p.getPlayerState() == Player.PlayerState.IS_MOVING) {
            if (GraphBuilder.getGraph().get(p.getBoardPosition()).getLocation().distanceBetweenGeolocationInMetres(location) < 20) {
                p.setPlayerState(Player.PlayerState.IS_DONE);
                notifyPlayerDone(p);
                return true;
            }
        }

        return false;
    }

    private boolean canMisterXMove() {
        Player p = playerList.get(misterXId);
        Node n = GraphBuilder.getGraph().get(p.getBoardPosition());

        for(Node neighbour : n.getNeighbours()) {
            if(isFree(neighbour.getId())) return true;
        }
        return false;
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
                turnState = PLAYER_TURN;
                startPlayerTurn();
            }
        } else {
            if(allPlayersAreDone()) {
                if(isMisterXCaught()) {
                    notifyPlayerGameWon();
                    finishGame();
                } else if(turnCounter >= 12) {
                    notifyPlayerGameLost();
                } else { startMisterXTurn(); };
            }
        }
    }

    private void finishGame() {
        gameState = GAMESTATE_OVER;
    }


    private void startMisterXTurn() {
        playerList.get(misterXId).setPlayerState(Player.PlayerState.IS_SELECTING);
        turnState = X_TURN;

        turnCounter++;

        if(turnCounter == 2 || turnCounter == 5 || turnCounter == 8) {
            broadcastMisterXPosition();
        }

        if(canMisterXMove()) {
            notifyMisterXStartTurn();
        } else {
            notifyMisterXSurrounded();
        }
    }


    private boolean allPlayersAreDone() {
        for(Player p : playerList) {
            if(!p.isMisterX()) {
                if(p.getPlayerState() != Player.PlayerState.IS_DONE) return false;
            }
        }
        return true;
    }

    private void startPlayerTurn() {

        for(Player p: playerList){
            p.setPlayerState(Player.PlayerState.IS_SELECTING);
        }
        notifyPlayerTurnStart();
    }


    public void assignRandomPositions() {
        ArrayList<Node> graph = GraphBuilder.getGraph();

        int size = graph.size();
        Player misterX = playerList.get(misterXId);
        int randomNumber = new Random().nextInt(size);
        misterX.setBoardPosition(graph.get(randomNumber).getId());

        for(Player p : playerList) {
            if(!p.isMisterX()) {
                assignPlayerRandomPosition(p,graph);
            }
        }
    }


    private void assignPlayerRandomPosition(Player p,ArrayList<Node> g) {
        int misterXPositionId = playerList.get(misterXId).getBoardPosition();
        int randomNodeId = new Random().nextInt(g.size());
        System.out.println(randomNodeId);
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




    private Player getPlayerByFirebaseToken(String firebaseToken) throws PlayerNotFoundException {
        for(Player p : playerList) {
            if(p.getFirebaseToken().equals(firebaseToken)) return p;
        }
        throw new PlayerNotFoundException("Player with fireBaseToken"+ firebaseToken +" was not found");
    }

    public GameStateBean getGameState(boolean misterX) {
        GameStateBean gsb = new GameStateBean();

        for(Player p : playerList) {
           gsb.addPlayer(p);
        }

        gsb.setGameState(gameState);
        gsb.setGameWon(gameWon);
        gsb.setTurnCounter(turnCounter);
        gsb.setTurnState(turnState);

        return gsb;
    }

    public String getGameStateAsJson(boolean misterX) {
        String object = new Gson().toJson(getGameState(misterX));
        return object;
    }

    public byte getGameState() {
        return gameState;
    }


    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public String getName() {
        return gameName;
    }

    public String getCreator() {
        return creatorName;
    }


    //TODO: Derzeit eine Methode für alle Pushnotification, folgendes Muster wollen wir aber :
    // notfyPlayer(Wen?, Was?)
    // Wen: Alle, Alle außer einem, Nur Mister X, Alles außer Mister X, Einen
    // Was: new DataField().addValue(TYPE, value).addValue(TYPE, value;


    public void sendChatMessage(String token, String message) throws PlayerNotFoundException {
        Player p = getPlayerByFirebaseToken(token);

        for(Player e : playerList) {
                new PushNotificationBuilder()
                        .addRecipient(e.getFirebaseToken())
                        .setNotificationType(PushNotificationBuilder.PushNotificationType.LOBBY_PLAYER_MESSAGE)
                        .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_MESSAGE, message)
                        .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_NAME, p.getName())
                        .addDataAttribute(PushNotificationBuilder.DataType.TIME_STAMP, new SimpleDateFormat("HH.mm").format(new Date()))
                        .push();

        }
    }

    private void notifyPlayerDone(Player p) {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_POSITION_REACHED)
                    .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_SELECT_ID, playerList.indexOf(p))
                    .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_SELECT_POSITION, p.getBoardPosition())
                    .push();
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

    private void notifyPlayerTurnStart() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_TURN_START_PLAYER)
                    .push();
        }
    }


    private void notifyMisterXSurrounded() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.GAME_X_SURROUNDED)
                    .addDataAttribute(PushNotificationBuilder.DataType.MISTER_X_POSITION, playerList.get(misterXId).getBoardPosition())
                    .push();
        }
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



    private void notifyPlayerGameStart() {
        for(Player q : playerList) {
            new PushNotificationBuilder()
                    .addRecipient(q.getFirebaseToken())
                    .setNotificationType(PushNotificationBuilder.PushNotificationType.LOBBY_GAME_START)
                    .addDataAttribute(PushNotificationBuilder.DataType.GAME_STATE, getGameStateAsJson(q.isMisterX()))
                    .addDataAttribute(PushNotificationBuilder.DataType.ARE_YOU_MISTER_X, q.isMisterX())
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
                        .addDataAttribute(PushNotificationBuilder.DataType.PLAYER_NAME, p.getName())
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

    public boolean isMisterXCaught() {
        System.out.println("Comparing stuff");
        Player p = playerList.get(misterXId);
        for(Player q : playerList) {
            if(!q.isMisterX()) {
                if(q.getBoardPosition() == p.getBoardPosition()) {
                    gameWon = true;
                    return gameWon;
                }
            }
        }
        return gameWon;
    }
}
