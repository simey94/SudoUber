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
        putDouble(spEditor, "pickupLat", journey.pickupLat);
        putDouble(spEditor, "pickupLong", journey.pickupLong);
        putDouble(spEditor, "destinationLat", journey.destinationLat);
        putDouble(spEditor, "destinationLong", journey.destinationLong);
        spEditor.putString("timing", journey.timing);
        spEditor.putString("payment", journey.payment);
        spEditor.putInt("clientID", journey.clientID);
        // commit changes
        spEditor.commit();
    }


    SharedPreferences.Editor putDouble(final SharedPreferences.Editor edit, final String key, final double value) {
        return edit.putLong(key, Double.doubleToRawLongBits(value));
    }

    double getDouble(final SharedPreferences prefs, final String key, final double defaultValue) {
        return Double.longBitsToDouble(prefs.getLong(key, Double.doubleToLongBits(defaultValue)));
    }

}
