package com.example.cs3301.cs3301_practical;


import android.content.Context;
import android.content.SharedPreferences;

public class JourneyLocalStore {
    public static final String SP_NAME = "journeyDetails";
    SharedPreferences JourneyLocalDatabase;
    int clientID;

    // Constructor
    public JourneyLocalStore(Context context, int clientID) {
        // 0 for default value
        this.clientID = clientID;
        JourneyLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public Client getClient(Client client) {
        return client;
    }

    public void storeJourneyData(Journey journey) {
        // allows edits to shared preferences
        SharedPreferences.Editor spEditor = JourneyLocalDatabase.edit();
        spEditor.putString("pickup", journey.pickup);
        spEditor.putString("destination", journey.destination);
        spEditor.putInt("timing", journey.timing);
        spEditor.putString("payment", journey.payment);
        spEditor.putInt("clientID", clientID);
        // commit changes
        spEditor.commit();
    }

    public Journey getClientJourney() {
        String pickup = JourneyLocalDatabase.getString("pickup", "");
        String destination = JourneyLocalDatabase.getString("destination", "");
        int timing = JourneyLocalDatabase.getInt("timing", 4);
        String payment = JourneyLocalDatabase.getString("payment", "");

        Journey storedJourney = new Journey(pickup, destination, timing, payment, clientID);
        return storedJourney;
    }

    // If logged in called with true & if logged out called with false
    public void setClientLoggedIn(boolean loggedIn) {
        SharedPreferences.Editor spEditor = JourneyLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public void clearClientData() {
        SharedPreferences.Editor spEditor = JourneyLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getClientLoggedin() {
        if (JourneyLocalDatabase.getBoolean("loggedIn", false) == true) {
            // user is logged in
            return true;
        } else {
            // user is logged out
            return false;
        }

    }
}
