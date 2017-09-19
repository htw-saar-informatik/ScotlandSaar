package com.denweisenseel.scotlandsaarexperimental.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by denwe on 25.07.2017.
 */

public class GameListInfoParcelable implements Parcelable {

    private String creatorName;
    private int playerCount;
    private int maxPlayerSize;
    private long gameId;

    public GameListInfoParcelable(String creatorName, int playerCount, int maxPlayerSize, long gameId) {
        this.creatorName = creatorName;
        this.playerCount = playerCount;
        this.maxPlayerSize = maxPlayerSize;
        this.gameId = gameId;
    }

    protected GameListInfoParcelable(Parcel in) {
        this.creatorName = in.readString();
        this.playerCount = in.readInt();
        this.maxPlayerSize = in.readInt();
        this.gameId = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(creatorName);
        dest.writeInt(playerCount);
        dest.writeInt(maxPlayerSize);
        dest.writeLong(gameId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<GameListInfoParcelable> CREATOR = new Creator<GameListInfoParcelable>() {
        @Override
        public GameListInfoParcelable createFromParcel(Parcel in) {
            return new GameListInfoParcelable(in);
        }

        @Override
        public GameListInfoParcelable[] newArray(int size) {
            return new GameListInfoParcelable[size];
        }
    };

    public String getName() {
        return creatorName;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public String toString() {
        return creatorName;
    }

    public long getGameId() {
        return gameId;
    }
}
