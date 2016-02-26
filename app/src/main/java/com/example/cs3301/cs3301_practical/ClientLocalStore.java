package com.example.cs3301.cs3301_practical;

import android.content.Context;
import android.content.SharedPreferences;


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
        spEditor.putInt("id", client.id);
        spEditor.putString("name", client.name);
        spEditor.putString("username", client.username);
        spEditor.putString("password", client.password);
        spEditor.putInt("age", client.age);
        // commit changes
        spEditor.commit();
    }

    public Client getLoggedInClient(){
        int id = clientLocalDatabase.getInt("id", -1);
        String name = clientLocalDatabase.getString("name", "");
        String username = clientLocalDatabase.getString("username", "");
        String password = clientLocalDatabase.getString("password", "");
        int age = clientLocalDatabase.getInt("age", -1);

        Client storedClient = new Client(id, name, username, password, age);

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

    public boolean isClientLoggedIn() {
        // user is logged out
        // user is logged in
        if(clientLocalDatabase.getBoolean("loggedIn", false)) return true;
        else return false;
    }

}
