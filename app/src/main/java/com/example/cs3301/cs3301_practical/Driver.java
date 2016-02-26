package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Driver table in database.
 */
public class Driver {
    int id, rating;
    boolean isAval;
    double lat, posLong;
    String name;

    public Driver(int id, String name, int rating, boolean isAval, double lat, double posLong) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.isAval = isAval;
        this.lat = lat;
        this.posLong = posLong;
    }
}
