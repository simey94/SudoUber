package com.example.cs3301.cs3301_practical;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
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

    /* Fetch and Store Client data */

    public void storeClientDataInBackground(Client client, GetClientCallBack clientCallBack){
        progressDialog.show();
        new StoreClientDataAsyncTask(client,clientCallBack).execute();
    }

    public void fetchClientDataInBackground(Client client, GetClientCallBack callBack){
        progressDialog.show();
        new fetchClientDataAsyncTask(client, callBack).execute();
    }

    /* Stores Order Data on the Server */

    public void storeJourneyDataInBackground(Journey journey, GetJourneyCallBack callback) {
        progressDialog.show();
        new StoreOrderDataAsyncTask(journey, callback).execute();
    }

    public void fetchJourneyDataInBackground(Client client, GetJourneyCallBack journeyCallBack) {
        progressDialog.show();
        new fetchJourneyDataAsyncTask(client, journeyCallBack).execute();
    }

    /* Store and Fetch Driver Details */
    public void storeDriverDataInBackground(Driver driver, GetDriverCallBack driverCallBack) {
        progressDialog.show();
        //new storeDriverDataInBackground(driver,driverCallBack).execute();
    }

    public void fetchDriverDataInBackground(Driver driver, GetDriverCallBack driverCallBack) {
        progressDialog.show();
        new fetchDriverDataAsyncTask(driver, driverCallBack).execute();
    }


    public class StoreOrderDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Journey journey;
        GetJourneyCallBack callback;

        public StoreOrderDataAsyncTask(Journey journey, GetJourneyCallBack callback) {
            this.journey = journey;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("pickupLat", String.valueOf(journey.pickupLat));
            dataToSend.put("pickupLong", String.valueOf(journey.pickupLong));
            dataToSend.put("destinationLat", String.valueOf(journey.destinationLat));
            dataToSend.put("destinationLong", String.valueOf(journey.destinationLong));
            dataToSend.put("timing", journey.timing);
            dataToSend.put("payment", journey.payment);
            dataToSend.put("clientID", String.valueOf(journey.clientID));

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;

            try {
                URL url = new URL(SERVER_ADDRESS + "RegisterJourney.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();

                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", line);

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            progressDialog.dismiss();
            callback.saveJourney(null);
        }
    }

    public class fetchJourneyDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Journey>> {
        Client client;
        GetJourneyCallBack journeyCallBack;

        public fetchJourneyDataAsyncTask(Client client, GetJourneyCallBack journeyCallBack) {
            this.client = client;
            this.journeyCallBack = journeyCallBack;
        }

        @Override
        protected ArrayList<Journey> doInBackground(Void... params) {

            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("clientID", String.valueOf(client.id));

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            ArrayList<Journey> returnedJournies = new ArrayList<>();

            try {
                URL url = new URL(SERVER_ADDRESS + "FetchJourneyData.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();
                Log.e("fetch", "The values received in the store part are as follows:");
                Log.e("fetch", line);

                if (line.length() > 10) {
                    JSONArray jsonArray = new JSONArray(line);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Double pickupLat = jsonObject.getDouble("pickupLat");
                        Double pickupLong = jsonObject.getDouble("pickupLong");
                        Double destinationLat = jsonObject.getDouble("destinationLat");
                        Double destinationLong = jsonObject.getDouble("destinationLong");
                        String timing = jsonObject.getString("timing");
                        String payment = jsonObject.getString("payment");
                        int journeyID = jsonObject.getInt("id");
                        Journey journey = new Journey(pickupLat, pickupLong, destinationLat, destinationLong, timing, payment, journeyID);
                        returnedJournies.add(journey);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//            for(int i =0; i<returnedJournies.size(); i++){
//                Log.e("Array list vals", String.valueOf(returnedJournies.get(i).clientID));
//            }
            return returnedJournies;
        }


        @Override
        protected void onPostExecute(ArrayList<Journey> returnedJournies) {
            super.onPostExecute(returnedJournies);
            journeyCallBack.getJournies(returnedJournies);
            progressDialog.dismiss();
        }

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
                    int id = jsonObject.getInt("id");
                    returnedClient = new Client(id, name, client.username, client.password, age);
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

        @Override
        protected void onPostExecute(Client returnedClient) {
            progressDialog.dismiss();
            clientCallBack.done(returnedClient);
            super.onPostExecute(returnedClient);
        }

    }

    public class fetchDriverDataAsyncTask extends AsyncTask<Void, Void, Driver> {
        Driver driver;
        GetDriverCallBack driverCallBack;

        public fetchDriverDataAsyncTask(Driver driver, GetDriverCallBack driverCallBack) {
            this.driver = driver;
            this.driverCallBack = driverCallBack;
        }

        @Override
        protected Driver doInBackground(Void... params) {
            // id, name, rating, lat, long
            Map<String, String> dataToSend = new HashMap<>();
            //dataToSend.put("id", driver.id);
            dataToSend.put("name", "" + driver.name);
            dataToSend.put("rating", "" + driver.rating);
            dataToSend.put("lat", driver.lat);
            dataToSend.put("posLong", "" + driver.posLong);

            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;
            Driver returnedDriver = null;

            try {
                URL url = new URL(SERVER_ADDRESS + "FetchDriverData.php");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("POST");
                con.setDoOutput(true);

                OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());

                writer.write(encodedStr);
                writer.flush();

                StringBuilder sb = new StringBuilder();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();
                Log.i("custom_check", "The values received in the store part are as follows:");
                Log.i("custom_check", line);
                Log.i("LENGTH", line.length() + "");

                if (line.length() > 10) {
                    JSONObject jsonObject = new JSONObject(line);
                    String name = jsonObject.getString("name");
                    String rating = jsonObject.getString("rating");
                    String lat = jsonObject.getString("lat");
                    String posLong = jsonObject.getString("posLong");

                    // TODO: ID maybe
                    returnedDriver = new Driver(name, rating, lat, posLong);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return returnedDriver;
        }

        @Override
        protected void onPostExecute(Driver returnedDriver) {
            progressDialog.dismiss();
            driverCallBack.done(returnedDriver);
            super.onPostExecute(returnedDriver);
        }
    }

    protected String getEncodedData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (String key : data.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(data.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (sb.length() > 0)
                sb.append("&");

            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }
}
