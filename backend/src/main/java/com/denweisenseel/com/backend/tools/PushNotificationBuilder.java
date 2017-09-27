package com.denweisenseel.com.backend.tools;

import org.json.simple.JSONObject;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

/**
 * Created by denwe on 16.09.2017.
 */

public class PushNotificationBuilder {

    PushNotification notification;

    public PushNotificationBuilder() {
        notification = new PushNotification();
    }

    public PushNotificationBuilder addRecipient(String fireBaseToken) {
        notification.addRequestAttribute("to", fireBaseToken);
        return this;
    }

    public PushNotificationBuilder setNotificationType(PushNotificationType type) {
        notification.addDataAttribute("type", type.type);
        return this;
    }

    public PushNotificationBuilder addDataAttribute(String key, Object value) {
        notification.addDataAttribute(key,value);
        return this;
    };

    public void push() {
        notification.push();
    }

    public class DataType {
        public static final String MISTER_X_POSITION = "xPosition";
        public static final String PLAYER_SELECT_POSITION ="playerPosition";
        public static final String PLAYER_SELECT_ID = "playerPositionId";
        public static final String PLAYER_NAME = "playerName";
        public static final String PLAYER_MESSAGE = "playerMessage";
        public static final String TIME_STAMP = "timeStamp";
        public static final String GAME_STATE = "gameState";
        public static final String ARE_YOU_MISTER_X = "isMisterX";
        public static final String PLAYER_ID = "playerId";
    }


    class PushNotification {

        private HashMap<String, Object> requestAttributes = new HashMap<String, Object>();
        private HashMap<String, Object> notificationAttributes = new HashMap<String,Object>();
        private HashMap<String, Object> dataAttributes = new HashMap<String,Object>();

        public PushNotification(){

        }

        public PushNotification(HashMap<String,Object> requestAttributes, HashMap<String,Object> notificationAttributes, HashMap<String,Object> dataAttributes ){
            this.dataAttributes = dataAttributes;
            this.requestAttributes = requestAttributes;
            this.notificationAttributes = notificationAttributes;
        }

        public PushNotification getCopy() {
            return new PushNotification(requestAttributes,notificationAttributes,dataAttributes);
        }

        public String toJSON(){

            JSONObject obj = new JSONObject();

            JSONObject not = new JSONObject();
            not.putAll(notificationAttributes);

            obj.put("notification", not);

            obj.putAll(requestAttributes);

            JSONObject data = new JSONObject();
            data.putAll(dataAttributes);
            obj.put("data", data);

            return obj.toString();
        }


        public PushNotification addNotificationAttribute(String key, Object value){
            notificationAttributes.put(key, value);
            return this;
        }

        public PushNotification addRequestAttribute(String key, Object value){
            requestAttributes.put(key, value);
            return this;
        }

        public PushNotification addDataAttribute(String key, Object value) {
            dataAttributes.put(key,value);
            return this;
        }

        public void push() {
            PushServer.push(this);
        }
    }


    private static class PushServer {
        final static String FIREBASE_SERVER_KEY = "AAAAfvb3Lj0:APA91bEl88SNgryGHl9tkuJJZz_7l2kUJa929HCiCJ6RgXBRXBlMMimMjDwB6vM3JEWt-xbvDWljEWVyYw8T6htgWpn5F75bQOT9hEp1YmeEqYtvKTdMWjLz_h644Cfph4tZPNuOdd_N";
        final static String API_URL = "https://fcm.googleapis.com/fcm/send";

        public static void push(PushNotification n) {
            if(FIREBASE_SERVER_KEY == null){
                System.err.println("No Server-Key has been defined for this.");
            }

            HttpURLConnection con = null;
            try{
                String url = API_URL;

                URL obj = new URL(url);
                con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");

                con.setRequestProperty("Authorization", "key="+FIREBASE_SERVER_KEY);
                con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");


                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
                writer.write(n.toJSON());

                writer.close();

                con.connect();
                ByteArrayInputStream t = (ByteArrayInputStream) con.getContent();
                int cone = t.available();
                byte[] bytes = new byte[cone];
                t.read(bytes,0,cone);
                String s = new String(bytes, StandardCharsets.UTF_8);


            }
            catch(Exception e){
                e.printStackTrace();
            }

        }
    }


    public enum PushNotificationType {

        //GAME related

        GAME_POSITION_REACHED("REACHED_POSITION"),
        GAME_POSITION_SELECTED("SELECTED_POSITION"),
        GAME_TURN_START_PLAYER("TURN_START_PLAYER"),
        GAME_TURN_START_X("TURN_START_X"),
        GAME_MISTER_X("YOU_MISTER_X"),

        //LOBBY related
        LOBBY_PLAYER_JOIN("PLAYER_JOIN"),
        LOBBY_PLAYER_LEAVE("PLAYER_LEFT"),
        LOBBY_PLAYER_MESSAGE("LOBBY_MESSAGE"),
        LOBBY_GAME_START("GAME_START"),
        GAME_REVEAL_X("REVEAL_X"),
        GAME_WON("GAME_WON"),
        GAME_LOST("GAME_LOST"),
        GAME_X_SURROUNDED("MISTER_X_SURROUNDED"),


        //CHAT related
        CHAT_NEW_MESSAGE("NEW_MESSAGE");

        private String type;

        PushNotificationType(String type) {
            this.type = type;
        }
    }
}
