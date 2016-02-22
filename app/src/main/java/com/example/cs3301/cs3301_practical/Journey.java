package com.example.cs3301.cs3301_practical;

/**
 * Class to represent entry in Journey table in database.
 */
public class Journey {
    String pickup, destination, timing, payment;

    public Journey(String pickup, String destination, String timing, String payment) {
        this.pickup = pickup;
        this.destination = destination;
        this.timing = timing;
        this.payment = payment;
    }

}
