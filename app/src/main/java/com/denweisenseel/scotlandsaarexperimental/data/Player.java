package com.denweisenseel.scotlandsaarexperimental.data;

import android.util.Log;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by denwe on 20.09.2017.
 */

public class Player {


    private int boardPosition;
    private String name;
    private Circle marker;
    private int id;
    private boolean misterX;

    public void setBoardPosition(int boardPosition) {
        this.boardPosition = boardPosition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBoardPosition() {
        return boardPosition;
    }

    public String getName() {
        return name;
    }

    public void setMarker(Circle marker) {
        this.marker = marker;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Circle getMarker() {

        Log.e("Player", "Circled of "+name+" has been accessed"); return marker;
    }

    public void setMisterX(boolean misterX) {
        this.misterX = misterX;
    }

    public boolean isMisterX() {
        return misterX;
    }
}
