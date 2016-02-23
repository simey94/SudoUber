package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Journey table in database.
 */
public class Journey {
    String pickup, destination, payment;
    int timing, clientID;

    public Journey(String pickup, String destination, int timing, String payment, int clientID) {
        this.pickup = pickup;
        this.destination = destination;
        this.timing = timing;
        this.payment = payment;
        this.clientID = clientID;
    }

    public Journey(int clientID) {
        this.clientID = clientID;
    }
}
