package com.denweisenseel.scotlandsaarexperimental.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.denweisenseel.scotlandsaarexperimental.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PushMessageHandlerService extends FirebaseMessagingService {



    private final String TAG = "PUSH MESSAGE";



    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            Map<String, String> m = remoteMessage.getData();

            //LOBBY:

            if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.LOBBY_PLAYER_JOIN))) {
                String[] data = {m.get(getString(R.string.LOBBY_PLAYER_JOIN_PLAYER_NAME))};
                forwardToLobby(getString(R.string.LOBBY_PLAYER_JOIN),data);
                Log.i(TAG, "Player "+ m.get(getString(R.string.LOBBY_PLAYER_JOIN_PLAYER_NAME)) + " joined lobby.");

            } else if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.LOBBY_PLAYER_MESSAGE))) {
                String message = m.get(getString(R.string.PLAYER_MESSAGE));
                String name = m.get(getString(R.string.PLAYER_NAME));
                String stamp = m.get(getString(R.string.TIME_STAMP));
                String[] data = {message,name,stamp};
                forwardToLobby(getString(R.string.LOBBY_PLAYER_MESSAGE),data);
                //notifyUser(name, message);

            } else if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.LOBBY_GAME_START))) {
                forwardToLobby(getString(R.string.LOBBY_GAME_START),   null);
            } else if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.GAME_TURN_START_X))) {

            }


            //GAME



        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void notifyUser(String name, String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle(name)
                        .setContentText(message);
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(001, mBuilder.build());
    }

    private void forwardToLobby(String type, String[] data) {
        Intent i = new Intent(type);

        if(data != null) {
            List l = Arrays.asList(data);
            ArrayList<String> list = new ArrayList<>();
            list.addAll(l);
            i.putStringArrayListExtra(getString(R.string.BROADCAST_DATA), list);
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }


}
