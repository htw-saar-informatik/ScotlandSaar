package com.denweisenseel.scotlandsaarexperimental.data;


import android.graphics.Color;
import android.util.Log;

import com.denweisenseel.scotlandsaarexperimental.data.Player;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by denwe on 20.09.2017.
 */

public class GameModel {

    private HashMap<Integer, Marker> markerMap = new HashMap();
    private ArrayList<Player> playerList = new ArrayList<>();
    private int id;
    private boolean misterX;
    private Graph graph;
    private Circle misterXCircle;
    private String TAG = "GameModel";

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Player getPlayerById(int playerId) throws Exception {
        for(Player p : playerList) {
            Log.v("PLAYERLOGGING", String.valueOf(p.getId()) + "   "  + playerId);
            if(p.getId() == playerId) {
                return p;
            }
        }
        throw new Exception("NO PLAYERID" + id);
    }

    public boolean isMisterX() {
        return misterX;
    }

    public void setMisterX(boolean misterX) {
        this.misterX = misterX;
    }

    public Player getMisterXPlayer() throws Exception {
        for(Player p : playerList) {
            if(p.isMisterX()) return p;
        }
        throw new Exception("Player not found");
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;

    }

    public Circle getMisterXCircle() {
        return misterXCircle;
    }

    public void setMisterXCircle(Circle misterXCircle) {
        this.misterXCircle = misterXCircle;
    }


    public void updatePlayerReachedMarker(int playerId, int boardPosition) {
        try {
            getPlayerById(playerId).getMarker().setCenter(graph.getNodeById(boardPosition).getPosition());
            getPlayerById(playerId).getMarker().setStrokeColor(Color.BLACK);
            Log.v(TAG, "Placed "+getPlayerById(playerId).getName()+" on " + boardPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePlayerSelection(int playerId, int boardPosition) {
        try {
            getPlayerById(playerId).getMarker()
                    .setCenter(graph.getNodeById(boardPosition).getPosition());
            getPlayerById(playerId).getMarker()
                    .setStrokeColor(Color.RED);
            Log.v(TAG, "Placed " + getPlayerById(playerId).getMarker().getStrokeColor() + " on " + boardPosition);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
