package com.denweisenseel.com.backend.beans;

/**
 * Created by denwe on 22.09.2017.
 */

public class MakeMoveResponseBean {

    boolean success;
    String  data;
    private int positionId;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setPositionId(int positionId) {
        this.positionId = positionId;
    }

    public int getPositionId() {
        return positionId;
    }


}
