package com.denweisenseel.com.backend;

import com.denweisenseel.com.backend.exceptions.PlayerNotFoundException;

/**
 * Created by denwe on 16.09.2017.
 */

public class Test {

    @org.junit.Test
    public void testGame() throws Exception, PlayerNotFoundException {

        GameBoard b = new GameBoard();
        b.createGame("123","Dennis", "GAME");
        b.joinGame("321","isnneD");

        b.makeMove("123",30);
    }


}
