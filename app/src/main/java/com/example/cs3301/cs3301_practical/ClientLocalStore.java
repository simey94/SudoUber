package com.example.cs3301.cs3301_practical;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by michaelsime on 20/02/16.
 */
public class ClientLocalStore {
    public static final String SP_NAME = "clientDetails";
    SharedPreferences clientLocalDatabase;

    public ClientLocalStore(Context context){
        // 0 for default value
        clientLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeClientData(Client client){
        // allows edits to shared preferences
        SharedPreferences.Editor spEditor = clientLocalDatabase.edit();
        spEditor.putString("name", client.name);
        spEditor.putString("username", client.username);
        spEditor.putString("password", client.password);
        spEditor.putInt("age", client.age);
        // commit changes
        spEditor.commit();
    }

    public Client getLoggedInClient(){
        String name = clientLocalDatabase.getString("name", "");
        String username = clientLocalDatabase.getString("username", "");
        String password = clientLocalDatabase.getString("password", "");
        int age = clientLocalDatabase.getInt("age", -1);

        Client storedClient = new Client(name, username, password, age);

        return storedClient;
    }

    // If logged in called with true & if logged out called with false
    public void setClientLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = clientLocalDatabase.edit();
        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();
    }

    public void clearClientData(){
        SharedPreferences.Editor spEditor = clientLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getClientLoggedin(){
        if(clientLocalDatabase.getBoolean("loggedIn", false) == true){
            // user is logged in
            return true;
        } else {
            // user is logged out
            return false;
        }

    }

}
