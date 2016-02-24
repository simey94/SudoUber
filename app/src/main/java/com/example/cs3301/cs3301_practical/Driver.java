package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Driver table in database.
 */
public class Driver {
    String id, name, rating, lat, posLong;

    public Driver(String name, String rating, String lat, String posLong) {
        this.name = name;
        this.rating = rating;
        this.lat = lat;
        this.posLong = posLong;
    }
}
