package com.denweisenseel.scotlandsaarexperimental.data;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by denwe on 20.09.2017.
 */

public class Player {


    private int boardPosition;
    private String name;
    private Marker marker;

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

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
