package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Driver table in database.
 */
public class Driver {
    String name, rating, posistion;

    public Driver(String name, String rating, String posistion) {
        this.name = name;
        this.rating = rating;
        this.posistion = posistion;
    }
}
