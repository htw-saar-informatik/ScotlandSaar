/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.denweisenseel.com.backend;

import com.denweisenseel.com.backend.beans.GameStateBean;
import com.denweisenseel.com.backend.beans.ResponseBean;
import com.denweisenseel.com.backend.data.Geolocation;
import com.denweisenseel.com.backend.data.Player;
import com.denweisenseel.com.backend.exceptions.PlayerNotFoundException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

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
        gameBoard.createGame(fireBaseToken,playerName,gameName);
        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        response.setSuccess(true);
        response.setGameId(gameBoard.id);
        return response;
    }

    @ApiMethod(name = "joinGame")
    public ResponseBean joinGame(@Named("id") long id,@Named("fireBaseToken") String fireBaseToken, @Named("playerName") String playerName) {
        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        boolean success = gameBoard.joinGame(fireBaseToken,playerName);
        System.out.println(success);

        ResponseBean response = new ResponseBean();
        String t = "";

        for(Player p : gameBoard.getPlayerList()) {
            t = t + p.getName() + " | ";
        }

        ofy().save().entity(gameBoard).now();
        response.setSuccess(success);
        return response;
    }


    @ApiMethod(name = "test")
    public GameStateBean test(@Named("id") long id) {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        GameStateBean response = gameBoard.getGameState(true);
        System.out.println(gameBoard.getPlayerList().size());

        return response;
    }


    @ApiMethod(name = "updatePosition")
    public ResponseBean updatePosition(@Named("id") long id,@Named("fireBaseToken") String fireBaseToken, Geolocation geolocation) throws PlayerNotFoundException {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();
        boolean success = gameBoard.updatePosition(fireBaseToken,geolocation);
        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        System.out.println(gameBoard.getPlayerList().size());
        response.setSuccess(success);

        return response;
    }




}
