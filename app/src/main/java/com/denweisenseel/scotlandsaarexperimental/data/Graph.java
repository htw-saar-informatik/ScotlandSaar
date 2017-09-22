package com.denweisenseel.scotlandsaarexperimental.data;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by denwe on 21.09.2017.
 */

public class Graph {


    private static final String TAG = "GRAPHBUILDER";
    ArrayList<Node> nodes;


    public void initialize(Context context, int resId) {
        String data = loadGraphFromFile(context, resId);
        try {
            JSONObject json = new JSONObject(data);
            Log.v(TAG, json.toString());
            nodes = new ArrayList<>();
            JSONArray array = json.getJSONArray("content");

            for(int i = 0; i < array.length(); i++) {
                JSONObject nodeJSON = array.getJSONObject(i);

                int id = nodeJSON.getInt("id");
                double latitude = nodeJSON.getDouble("lat");
                double longitude = nodeJSON.getDouble("lng");

                JSONArray neighbours = nodeJSON.getJSONArray("neighbours");

                Node n = new Node(id, latitude, longitude);

                for(int k=0; k < neighbours.length(); k++) {
                    n.addNeighbourId(neighbours.getInt(k));
                }
                nodes.add(n);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        for(Node n : nodes) {
            System.out.println(n);
        }
    }

    public String loadGraphFromFile(Context context, int resId) {
        InputStream stream = context.getResources().openRawResource(resId);
        InputStreamReader inputreader = new InputStreamReader(stream);
        BufferedReader buffreader = new BufferedReader(inputreader);
        String line;
        StringBuilder text = new StringBuilder();

        try {
            while (( line = buffreader.readLine()) != null) {
                text.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return text.toString();
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }


    public class Node {

        private int id;
        private LatLng position;
        private ArrayList<Integer> neighbours;

        public Node(int id, double latitude, double longitude) {
            this.id = id;
            position = new LatLng(latitude,longitude);
            neighbours = new ArrayList<Integer>();
        }

        public void addNeighbourId(int i) {
            neighbours.add(i);
        }

        @Override
        public String toString() {
            return "{id:"+id+","+position.toString()+","+neighbours.toString()+"}";
        }

        public LatLng getPosition() {
            return position;
        }

        public int getId() {

            return id;
        }


        public ArrayList<Integer> getNeighbours() {
            return neighbours;
        }
    }

}
