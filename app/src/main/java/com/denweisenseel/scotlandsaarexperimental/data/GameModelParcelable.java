package com.denweisenseel.scotlandsaarexperimental.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by denwe on 20.09.2017.
 */

public class GameModelParcelable implements Parcelable {


    protected GameModelParcelable(Parcel in) {

    }

    public static final Creator<GameModelParcelable> CREATOR = new Creator<GameModelParcelable>() {
        @Override
        public GameModelParcelable createFromParcel(Parcel in) {
            return new GameModelParcelable(in);
        }

        @Override
        public GameModelParcelable[] newArray(int size) {
            return new GameModelParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
