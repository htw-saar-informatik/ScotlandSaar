package com.denweisenseel.scotlandsaarexperimental.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
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

            // TODO: parse packet type better, later redo protocol to send bytes/ints instead of strings (which we did for readability)


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
                String args = remoteMessage.getData().toString();
                Log.i("TEST", args);
                forwardToMap(getString(R.string.LOBBY_GAME_START),  args);
            } else if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.GAME_TURN_START_X))) {
                forwardToMap(getString(R.string.GAME_TURN_START_X), null);
            } else if(m.get((getString(R.string.protocol_type))).equals(getString(R.string.TURN_START_PLAYER))) {
                forwardToMap(getString(R.string.TURN_START_PLAYER), null);
            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_POSITION_REACHED))) {
                int boardPosition = Integer.valueOf(m.get("playerPosition"));
                int playerId = Integer.valueOf(m.get("playerPositionId"));
                Log.i(TAG, "Player with" + playerId + " arrived at position "+boardPosition);
                sendPlayerUpdateToLobby(getString(R.string.GAME_POSITION_REACHED), playerId,boardPosition);
                //TODO: Send information to activity, update map
            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_POSITION_SELECTED))) {
                int boardPosition = Integer.valueOf(m.get("playerPosition"));
                int playerId = Integer.valueOf(m.get("playerPositionId"));
                Log.i(TAG, "Player with" + playerId + " selected position "+boardPosition);
                //TODO: Send information to activity, update map (Player  with id selected something)
            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_WON))) {
                Log.i(TAG, "Player caught Mister X");
                sendGameEndedToLobby(getString(R.string.GAME_WON));
                //TODO: Send information to activity, update map (Player  with id selected something)

            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_LOST))) {
                Log.i(TAG, "Mister X won!");
                //TODO: Send information to activity, update map (Player  with id selected something)
            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_REVEAL_X))) {
                int misterXPos = Integer.valueOf(m.get(getString(R.string.MISTER_X_POSITION)));
                Log.i(TAG, "Mister X revealed at " +misterXPos);
                //TODO: Send information to activity, update map (Player  with id selected something)
            } else if(m.get(getString(R.string.protocol_type)).equals(getString(R.string.GAME_X_SURROUNDED))) {
                int misterXPos = Integer.valueOf(m.get(getString(R.string.MISTER_X_POSITION)));
                Log.i(TAG, "Mister X can't move anymore, revealed at " +misterXPos);
                //TODO: Send information to activity, update map (Player  with id selected something)
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
    private  void sendGameEndedToLobby(String string){
        Intent i = new Intent(string);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);
    }

    private void sendPlayerUpdateToLobby(String string, int playerId, int boardPosition) {
        Intent i = new Intent(string);
        i.putExtra("playerId", playerId);
        i.putExtra("boardPosition", boardPosition);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

    }

    private void forwardToMap(String string, String args) {

        Intent i = new Intent(string);

        i.putExtra(getString(R.string.BROADCAST_DATA),args);
        LocalBroadcastManager.getInstance(this).sendBroadcast(i);

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
