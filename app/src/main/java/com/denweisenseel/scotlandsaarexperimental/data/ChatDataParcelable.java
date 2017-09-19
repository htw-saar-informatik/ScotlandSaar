package com.denweisenseel.scotlandsaarexperimental.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Parcelable for the ChatDataParcelable.
 * @author Andreas Bonny
 * @version 1.0
 */

public class ChatDataParcelable implements Parcelable {

    private String name;
    private String message;
    private String time;

    public ChatDataParcelable(String name, String message, String time) {
        this.name = name;
        this.message = message;
        this.time = time;
    }

    protected ChatDataParcelable(Parcel in) {
        this.name = in.readString();
        this.message = in.readString();
        this.time = in.readString();
    }

    public static final Creator<ChatDataParcelable> CREATOR = new Creator<ChatDataParcelable>() {
        @Override
        public ChatDataParcelable createFromParcel(Parcel in) {
            return new ChatDataParcelable(in);
        }

        @Override
        public ChatDataParcelable[] newArray(int size) {
            return new ChatDataParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(message);
        dest.writeString(time);
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }
}
