package com.example.cs3301.cs3301_practical;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
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

    public class StoreClientDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Client client;
        GetClientCallBack clientCallBack;

        public StoreClientDataAsyncTask(Client client, GetClientCallBack clientCallBack) {
            this.client = client;
            this.clientCallBack = clientCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {

            Map<String, String> dataToSend = new HashMap<>();
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
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
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

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            progressDialog.dismiss();
            clientCallBack.done(null);
            super.onPostExecute(aVoid);
        }
    }

    public void fetchClientDataInBackground(Client client, GetClientCallBack callBack){
        progressDialog.show();
        new fetchClientDataAsyncTask(client, callBack).execute();
    }

    public class fetchClientDataAsyncTask extends AsyncTask<Void, Void, Client> {
        Client client;
        GetClientCallBack clientCallBack;

        public fetchClientDataAsyncTask(Client client, GetClientCallBack clientCallBack) {
            this.client = client;
            this.clientCallBack = clientCallBack;
        }

        @Override
        protected Client doInBackground(Void... params) {

            Map<String, String> dataToSend = new HashMap<>();
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
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                line = sb.toString();

                if (line.length() > 10) {
                    JSONObject jsonObject = new JSONObject(line);
                    String name = jsonObject.getString("name");
                    int age = jsonObject.getInt("age");
                    int id = jsonObject.getInt("id");
                    returnedClient = new Client(id, name, client.username, client.password, age);
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

            return returnedClient;
        }

        @Override
        protected void onPostExecute(Client returnedClient) {
            progressDialog.dismiss();
            clientCallBack.done(returnedClient);
            super.onPostExecute(returnedClient);
        }

    }

    /* Stores Journey Data on the Server */

    public void storeJourneyDataInBackground(Journey journey, GetJourneyCallBack callback) {
        progressDialog.show();
        new StoreJourneyDataAsyncTask(journey, callback).execute();
    }

    public class StoreJourneyDataAsyncTask extends AsyncTask<Void, Void, Void> {
        Journey journey;
        GetJourneyCallBack callback;

        public StoreJourneyDataAsyncTask(Journey journey, GetJourneyCallBack callback) {
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


    public void fetchJourneyDataInBackground(Client client, GetJourneyCallBack journeyCallBack) {
        progressDialog.show();
        new fetchJourneyDataAsyncTask(client, journeyCallBack).execute();
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
            return returnedJournies;
        }


        @Override
        protected void onPostExecute(ArrayList<Journey> returnedJournies) {
            super.onPostExecute(returnedJournies);
            journeyCallBack.getJourneys(returnedJournies);
            progressDialog.dismiss();
        }

    }

    /* Fetch Driver on the Server */

    public void fetchDriverDataInBackground(Journey journey, GetDriverCallBack driverCallBack) {
        progressDialog.show();
        new fetchDriverDataAsyncTask(journey, driverCallBack).execute();
    }

    public class fetchDriverDataAsyncTask extends AsyncTask<Void, Void, ArrayList<Driver>> {
        Journey journey;
        GetDriverCallBack driverCallBack;

        public fetchDriverDataAsyncTask(Journey journey, GetDriverCallBack driverCallBack) {
            this.journey = journey;
            this.driverCallBack = driverCallBack;
        }

        @Override
        protected ArrayList<Driver> doInBackground(Void... params) {
            ArrayList<Driver> fetchedDrivers = new ArrayList<>();

            // id, name, rating, lat, long
            Map<String, String> dataToSend = new HashMap<>();
            dataToSend.put("lat", String.valueOf(journey.pickupLat));
            dataToSend.put("posLong", String.valueOf(journey.pickupLong));
            // radius of 30 miles
            dataToSend.put("radius", String.valueOf(50));


            String encodedStr = getEncodedData(dataToSend);
            BufferedReader reader = null;

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

                if (line.length() > 10) {
                    try {
                        JSONArray array = new JSONArray(line);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int id = obj.getInt("id");
                            String name = obj.getString("name");
                            int rating = obj.getInt("rating");
                            int isAvailable = obj.getInt("available");
                            boolean isA = (isAvailable == 1);
                            double lat = obj.getDouble("lat");
                            double posLong = obj.getDouble("posLong");
                            Driver driver = new Driver(id, name,
                                    rating, isA, lat, posLong);
                            fetchedDrivers.add(driver);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
            return fetchedDrivers;
        }

        @Override
        protected void onPostExecute(ArrayList<Driver> returnedDrivers) {
            super.onPostExecute(returnedDrivers);
            progressDialog.dismiss();
            driverCallBack.done(returnedDrivers);
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