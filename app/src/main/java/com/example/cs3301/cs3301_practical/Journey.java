package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Journey table in database.
 */
public class Journey {
    String timing, payment;
    Double pickupLat, pickupLong, destinationLat, destinationLong;
    int clientID;

    public Journey(Double pickupLat, Double pickupLong, Double destinationLat, Double destinationLong, String timing, String payment, int clientID) {
        this.pickupLat = pickupLat;
        this.pickupLong = pickupLong;
        this.destinationLat = destinationLat;
        this.destinationLong = destinationLong;
        this.timing = timing;
        this.payment = payment;
        this.clientID = clientID;
    }

    public Journey(int clientID) {
        this.clientID = clientID;
    }
}
