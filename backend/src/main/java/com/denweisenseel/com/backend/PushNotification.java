package com.denweisenseel.com.backend;

import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Created by denwe on 16.09.2017.
 */

public class PushNotification {


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

        JSONObject obj = new JSONObject(); // Parent object


        // create and add every notification attribute into its own json objects
        JSONObject not = new JSONObject();
        not.putAll(notificationAttributes);

        // add notification object to parent
        obj.put("notification", not);

        // add request attributes to parent
        obj.putAll(requestAttributes);

        JSONObject data = new JSONObject();
        data.putAll(dataAttributes);
        obj.put("data", data);



        //System.out.println(obj.toString());

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
    //TODO statt return this einfach return dataAttributes.put(key,value);?

/*    public void addRecipient(String playerToken) {
        Notification n = null;

    }*/

}
