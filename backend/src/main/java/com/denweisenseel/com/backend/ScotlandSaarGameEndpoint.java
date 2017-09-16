/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.denweisenseel.com.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Result;

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

    /**
     * A simple endpoint method that takes a name and says Hi back
     */

    @ApiMethod(name = "sayHi")
    public ResponseBean sayHi(@Named("name") String name) {
        ResponseBean response = new ResponseBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "createGame")
    public ResponseBean createGame(@Named("fireBaseToken") String fireBaseToken, @Named("playerName") String playerName, @Named("gameName") String gameName) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.createGame(fireBaseToken,playerName,gameName);
        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        response.setData("ResponseId: " + gameBoard.id);
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
        response.setData("Player:"+ t);
        return response;
    }


    @ApiMethod(name = "test")
    public GameStateBean test(@Named("id") long id) {

        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        GameStateBean response = gameBoard.getGameState(true);
        System.out.println(gameBoard.getPlayerList().size());

        return response;
    }




}
