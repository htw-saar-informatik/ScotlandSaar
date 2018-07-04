package com.denweisenseel.com.backend.data;


import java.util.ArrayList;

/**
 * Created by denwe on 23.07.2017.
 */

public class Node {
    int id;
    Geolocation location;
    ArrayList<Node> neighbours = new ArrayList<>();

    public Node(int id, Geolocation location) {
        this.id = id;
        this.location = location;
    }

    public void addNeighbour(Node node) {
        neighbours.add(node);
    }

    public String toString() {

        String s = "";
        for(Node n : neighbours) {
            s = s + n.getId() + ",";
        }

        return "[ID:"+id+" , "+ location.toString() + s;// + neighbours.toString() + "]";
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return location.getLongitude();
    }

    public double getLatitude() {
        return location.getLatitude();
    }

    public ArrayList<Node> getNeighbours() {
        return neighbours;
    }

    public boolean hasNeighbour(int id) {
        for(Node n: neighbours) {
            if(n.getId() == id) return true;
        }
        return false;
    }

    public Geolocation getLocation() {
        return location;
    }
}