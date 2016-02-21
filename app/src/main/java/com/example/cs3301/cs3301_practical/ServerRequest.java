package com.example.cs3301.cs3301_practical;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerRequest {

    ProgressDialog progressDialog;

    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "https://ms255.host.cs.st-andrews.ac.uk/";

    public ServerRequest(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing Request");
        progressDialog.setMessage("Please wait...");
    }

    public void storeClientDataInBackground(Client client, GetClientCallBack clientCallBack){
        progressDialog.show();
        new StoreClientDataAsyncTask(client,clientCallBack).execute();
    }

    public void fetchClientDataInBackground(Client client, GetClientCallBack callBack){
        progressDialog.show();
        new fetchClientDataAsyncTask(client, callBack).execute();
    }

    public class StoreClientDataAsyncTask extends AsyncTask<Void ,Void, Void >{
        Client client;
        GetClientCallBack clientCallBack;

        public StoreClientDataAsyncTask(Client client, GetClientCallBack clientCallBack){
            this.client = client;
            this.clientCallBack = clientCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("name", client.name);
            dataToSend.put("username", client.username);
            dataToSend.put("password", client.password);
            dataToSend.put("age", client.age + "");

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;

            try {
                URL url = new URL(SERVER_ADDRESS + "Register.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();

                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", line);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return null;
        }


        protected String getEncodedData(Map<String,String> data) {
            StringBuilder sb = new StringBuilder();
            for(String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(sb.length()>0)
                    sb.append("&");

                sb.append(key).append("=").append(value);
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            clientCallBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public class fetchClientDataAsyncTask extends AsyncTask<Void ,Void, Client > {
        Client client;
        GetClientCallBack clientCallBack;

        public fetchClientDataAsyncTask(Client client, GetClientCallBack clientCallBack){
            this.client = client;
            this.clientCallBack = clientCallBack;
        }

        @Override
        protected Client doInBackground(Void... params) {


            Map<String,String> dataToSend = new HashMap<>();
            dataToSend.put("name", client.name);
            dataToSend.put("age", client.age + "");
            dataToSend.put("username", client.username);
            dataToSend.put("password", client.password);

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            Client returnedClient = null;

            try {
                URL url = new URL(SERVER_ADDRESS + "FetchUserData.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check",line);
                Log.i("LENGTH",line.length() + "");

                if(line.length() > 10) {
                    JSONObject jsonObject = new JSONObject(line);
                    String name = jsonObject.getString("name");
                    int age = jsonObject.getInt("age");
                    returnedClient = new Client(name, client.username, client.password, age);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return returnedClient;
        }

        protected String getEncodedData(Map<String,String> data) {
            StringBuilder sb = new StringBuilder();
            for(String key : data.keySet()) {
                String value = null;
                try {
                    value = URLEncoder.encode(data.get(key), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                if(sb.length()>0)
                    sb.append("&");

                sb.append(key).append("=").append(value);
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(Client returnedClient) {
            progressDialog.dismiss();
            clientCallBack.done(returnedClient);
            super.onPostExecute(returnedClient);
        }

    }

}
