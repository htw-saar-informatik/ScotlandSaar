/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.denweisenseel.com.backend;

import com.denweisenseel.com.backend.beans.GameListBean;
import com.denweisenseel.com.backend.beans.GameStateBean;
import com.denweisenseel.com.backend.beans.MakeMoveResponseBean;
import com.denweisenseel.com.backend.beans.ResponseBean;
import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Player;
import com.denweisenseel.com.backend.exceptions.PlayerNotFoundException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * An endpoint class we are exposing
 */

@Api(
        name = "scotlandSaarAPI",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.com.denweisenseel.com",
                ownerName = "backend.com.denweisenseel.com",
                packagePath = ""
        )
)
public class ScotlandSaarGameEndpoint {


    @ApiMethod(name = "sayHi")
    public ResponseBean sayHi(@Named("name") String name) {
        ResponseBean response = new ResponseBean();
        response.setSuccess(true);

        return response;
    }

    @ApiMethod(name = "createGame")
    public ResponseBean createGame(@Named("fireBaseToken") String fireBaseToken, @Named("playerName") String playerName, @Named("gameName") String gameName) {

        GameBoard gameBoard = new GameBoard();
        int i = gameBoard.createGame(fireBaseToken,playerName,gameName);
        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        response.setSuccess(true);
        response.setGameId(gameBoard.id);
        response.setPlayerId(i);
        return response;
    }

    @ApiMethod(name = "joinGame")
    public ResponseBean joinGame(@Named("id") long id,@Named("fireBaseToken") String fireBaseToken, @Named("playerName") String playerName) {
        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        int playerId = gameBoard.joinGame(fireBaseToken,playerName);

        ResponseBean response = new ResponseBean();
        if(playerId != -1) {
            ArrayList<Player> playerInTheLobby = new ArrayList<>();
            for (int i = 0; i < gameBoard.getPlayerList().size(); i++){
                playerInTheLobby.add(gameBoard.getPlayerList().get(i));
            }
            response.setSuccess(true);
            response.setPlayerId(playerId);
            response.setPlayerInLobby(playerInTheLobby);
        } else response.setSuccess(false);
        ofy().save().entity(gameBoard).now();

        return response;
    }

    @ApiMethod(name = "startGame")
    public ResponseBean startGame(@Named("id") long id,@Named("fireBaseToken") String fireBaseToken) {
        ResponseBean response = new ResponseBean();
        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();
        if(gameBoard.startGame(fireBaseToken)) {
            ofy().save().entity(gameBoard).now();
            response.setSuccess(true);
            return response;
        } else {
            response.setSuccess(false);
            return response;
        }
    }


    @ApiMethod(name = "test")
    public GameStateBean test(@Named("id") long id) {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        GameStateBean response = gameBoard.getGameState(true);
        System.out.println(gameBoard.getPlayerList().size());

        return response;
    }

    @ApiMethod(name = "gameList")
    public ArrayList<GameListBean> getGameList() {

        ArrayList<GameListBean> gameList= new ArrayList<GameListBean>();


        List<GameBoard> games = ofy().load().type(GameBoard.class).list();

        for(GameBoard game : games) {
            GameListBean bean = new GameListBean();
            if(game.getGameState() == GameBoard.GAMESTATE_SETUP) {
                bean.setGameId(game.id);
                bean.setGameName(game.getName());
                bean.setHost(game.getCreator());
                bean.setPlayerCount(game.getPlayerList().size());
                gameList.add(bean);
            }
        }
        return gameList;
    }

    @ApiMethod(name = "sendChatMessage")
    public ResponseBean sendChatMessage(@Named("id") long id, @Named("fireBaseToken") String token, @Named("message") String message) throws PlayerNotFoundException {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();
        gameBoard.sendChatMessage(token,message);


        ResponseBean response = new ResponseBean();
        response.setSuccess(true);

        return response;
    }

    @ApiMethod(name = "getGameState")
    public GameStateBean getGameState(@Named("id") long id) {
        GameBoard board = ofy().load().type(GameBoard.class).id(id).now();
        return board.getGameState(true);
    }

    @ApiMethod(name = "makeMove")
    public MakeMoveResponseBean makeMove(@Named("id") long id, @Named("fireBaseToken") String token, @Named("targetPosition") int targetPosition) throws PlayerNotFoundException {
        GameBoard board = ofy().load().type(GameBoard.class).id(id).now();
        MakeMoveResponseBean bean;

        bean =  board.makeMove(token, targetPosition);
        ofy().save().entity(board).now();


        return bean;
    }

    @ApiMethod(name = "updatePosition")
    public ResponseBean updatePosition(@Named("id") long id, @Named("fireBaseToken") String token, @Named("latitude") double latitude, @Named("longitude") double longitude) throws PlayerNotFoundException {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();
        Geolocation geolocation = new Geolocation(latitude,longitude);
        boolean success = gameBoard.updatePosition(token,geolocation);
        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        System.out.println(gameBoard.getPlayerList().size());
        response.setSuccess(success);


        return response;
    }


}
