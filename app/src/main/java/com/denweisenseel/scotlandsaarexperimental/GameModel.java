package com.denweisenseel.scotlandsaarexperimental;


import com.denweisenseel.scotlandsaarexperimental.data.Player;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by denwe on 20.09.2017.
 */

public class GameModel {



    private HashMap<Integer, Marker> markerMap = new HashMap();
    private ArrayList<Player> playerList = new ArrayList<>();

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    public void addMarker(int id, Marker m) {
        markerMap.put(id,m);
    }

    public Marker getMarker(Integer i) {
        return markerMap.get(i);
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }

    public void addPlayer(Player p) {
        playerList.add(p);
    }
}
