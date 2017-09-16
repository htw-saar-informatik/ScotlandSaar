/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.denweisenseel.com.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.com.google.protos.gdata.proto2api.Core;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
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
    @ApiMethod(name = "createPlayer")
    public ResponseBean createPlayer(@Named("name") String name) {
        Player p = new Player();
        p.setName(name);
        Result<Key<Player>> result = ofy().save().entity(p);

        if(p.id == null) { System.out.println("Nein!");}

        result.now();

        if(p.id != null) { System.out.println("Ja!");}



        ResponseBean response = new ResponseBean();
        response.setData("Re"+ p.getName() + " Id: " + p.id);
        return response;
    }
    @ApiMethod(name = "getPlayer")
    public ResponseBean getPlayer(@Named("id") long name) {
        Player p = ofy().load().type(Player.class).id(name).now();
        ResponseBean response = new ResponseBean();
        response.setData("Re"+ p.getName() + " Id: " + p.id);
        return response;
    }

    @ApiMethod(name = "createGame")
    public ResponseBean createGame(@Named("name") String name) {
        GameBoard gameBoard = new GameBoard();
        Player p = new Player();
        p.setName(name);
        //gameBoard.addPlayer(p);

        ofy().save().entity(gameBoard).now();

        ResponseBean response = new ResponseBean();
        response.setData("Re"+ p.getName() + " Id: " + gameBoard.id);
        return response;
    }

    @ApiMethod(name = "loadGame")
    public ResponseBean loadGame(@Named("id") long id) {
        GameBoard gameBoard = ofy().load().type(GameBoard.class).id(id).now();

        ResponseBean response = new ResponseBean();
        String t = "";

        for(Player p : gameBoard.getPlayerList()) {
            t = t + p.getName() + " | ";
        }
        response.setData("Player:"+ t);
        return response;
    }


}
