package com.example.cs3301.cs3301_practical;


import android.content.Context;
import android.content.SharedPreferences;

public class JourneyLocalStore {
    public static final String SP_NAME = "journeyDetails";
    // SP allows storage of data on phone
    SharedPreferences JourneyLocalDatabase;
    int clientID;

    // Constructor
    public JourneyLocalStore(Context context, int clientID) {
        // 0 for default value
        this.clientID = clientID;
        JourneyLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeJourneyData(Journey journey) {
        // allows edits to shared preferences
        SharedPreferences.Editor spEditor = JourneyLocalDatabase.edit();
        spEditor.putString("pickup", journey.pickup);
        spEditor.putString("destination", journey.destination);
        spEditor.putString("timing", journey.timing);
        spEditor.putString("payment", journey.payment);
        spEditor.putInt("clientID", clientID);
        // commit changes
        spEditor.commit();
    }

    public Journey getClientJourney(int clientID) {
        String pickup = JourneyLocalDatabase.getString("pickup", "");
        String destination = JourneyLocalDatabase.getString("destination", "");
        String timing = JourneyLocalDatabase.getString("timing", "");
        String payment = JourneyLocalDatabase.getString("payment", "");

        Journey storedJourney = new Journey(pickup, destination, timing, payment, clientID);
        return storedJourney;
    }

}
