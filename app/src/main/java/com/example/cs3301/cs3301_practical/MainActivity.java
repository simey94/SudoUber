package com.example.cs3301.cs3301_practical;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, TimeDialogActivity.Communicator, AdapterView.OnItemSelectedListener {

    private GoogleMap mMap;
    // UI elements
    ImageButton ibDeletePickup, ibDeleteDest, ibHere, ibSearchPickup, ibSearchDes, ibTime, ibBookTaxi, ibAccount, ibHistory, ibMapType;
    PopupMenu popupMenu, histPopupMenu;
    EditText etName, etAge, etUsername, etFrom, etDestination;
    TextView tvWhen, tvDistance, tvTime, tvCost;
    Spinner spinner;

    Location currentLocation;
    Calendar whenDate;

    // Local database objects
    ClientLocalStore clientLocalStore;
    JourneyLocalStore journeyLocalStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Text fields
        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etFrom = (EditText) findViewById(R.id.etFrom);
        etFrom.setInputType(InputType.TYPE_CLASS_TEXT);
        etDestination = (EditText) findViewById(R.id.etDestination);
        etDestination.setInputType(InputType.TYPE_CLASS_TEXT);
        tvWhen = (TextView) findViewById(R.id.etWhen);
        tvWhen.setInputType(InputType.TYPE_CLASS_TEXT);
        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvDistance.setInputType(InputType.TYPE_CLASS_TEXT);
        tvDistance.setTextColor(ContextCompat.getColor(this, R.color.RED));
        tvTime = (TextView) findViewById(R.id.tvTime);
        tvTime.setInputType(InputType.TYPE_CLASS_TEXT);
        tvTime.setTextColor(ContextCompat.getColor(this, R.color.RED));
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvCost.setInputType(InputType.TYPE_CLASS_TEXT);
        tvCost.setTextColor(ContextCompat.getColor(this, R.color.RED));


        // Image buttons
        ibDeletePickup = (ImageButton) findViewById(R.id.ibDeletePickup);
        ibDeleteDest = (ImageButton) findViewById(R.id.ibDeleteDest);
        ibHere = (ImageButton) findViewById(R.id.ibHere);
        ibSearchPickup = (ImageButton) findViewById(R.id.ibSearchPickup);
        ibSearchDes = (ImageButton) findViewById(R.id.ibSearchDes);
        ibTime = (ImageButton) findViewById(R.id.ibTime);
        ibBookTaxi = (ImageButton) findViewById(R.id.ibBookTaxi);
        ibAccount = (ImageButton) findViewById(R.id.ibAccount);
        ibHistory = (ImageButton) findViewById(R.id.ibHistory);
        ibMapType = (ImageButton) findViewById(R.id.ibMapType);


        // Image click listeners
        ibDeletePickup.setOnClickListener(this);
        ibDeleteDest.setOnClickListener(this);
        ibHere.setOnClickListener(this);
        ibSearchPickup.setOnClickListener(this);
        ibSearchDes.setOnClickListener(this);
        ibTime.setOnClickListener(this);
        ibBookTaxi.setOnClickListener(this);
        ibAccount.setOnClickListener(this);
        ibHistory.setOnClickListener(this);
        ibMapType.setOnClickListener(this);

        clientLocalStore = new ClientLocalStore(this);
        journeyLocalStore = new JourneyLocalStore(this, clientLocalStore.getLoggedInClient().id);

        // Payment choices spinner
        spinner = (Spinner) findViewById(R.id.payment_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.payment_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!authenticate()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    private boolean authenticate() {
        // returns true if user is logged in
        return clientLocalStore.getClientLoggedin();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ibAccount:

                // Create drop down to show options
                popupMenu = new PopupMenu(this, ibHistory);
                MenuInflater menuInflater = popupMenu.getMenuInflater();
                menuInflater.inflate(R.menu.popup_actions, popupMenu.getMenu());

                //get client info
                Client client = clientLocalStore.getLoggedInClient();
                // loop through menu items
                popupMenu.getMenu().findItem(R.id.id_id).setTitle("ID: " + client.id);
                popupMenu.getMenu().findItem(R.id.id_name).setTitle("Name: " + client.name);
                popupMenu.getMenu().findItem(R.id.id_username).setTitle("Username: " + client.username);
                popupMenu.getMenu().findItem(R.id.id_age).setTitle("Age: " + client.age);
                popupMenu.getMenu().findItem(R.id.id_logout).setTitle("Logout");

                // click handler
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.id_logout) {
                            clientLocalStore.clearClientData();
                            clientLocalStore.setClientLoggedIn(false);
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                        return true;
                    }
                });
                popupMenu.show();
                break;

            case R.id.ibBookTaxi:
                String from = etFrom.getText().toString();
                String destination = etDestination.getText().toString();
                String strWhen = tvWhen.getText().toString();
                String payment = spinner.getSelectedItem().toString();
                int clientID = clientLocalStore.getLoggedInClient().id;

                // Maria DB format for insertion into db
                String dateFormat = "yyyy-MM-dd hh:mm:ss";
                SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.UK);
                String pickupTime = format.format(whenDate.getTime());

                // Input form validation
                if (isValidBookingDetails(from, destination, strWhen, payment)) {

                    double currentLong = currentLocation.getLongitude(), currentLat = currentLocation.getLatitude();
                    String finalAddress = getFullAddress(currentLat, currentLong);

                    // Get the values of Pickup and Dest
                    String strPickupAddress = etFrom.getText().toString();
                    String strDestinationAddress = etDestination.getText().toString();

                    // convert to lat lng vals
                    LatLng pickupLatLng = getLocationFromAddress(this, strPickupAddress);
                    LatLng destLatLng = getLocationFromAddress(this, strDestinationAddress);

                    if (finalAddress != null) {
                        Journey journey = new Journey(pickupLatLng.latitude, pickupLatLng.longitude, destLatLng.latitude, destLatLng.longitude, pickupTime, payment, clientID);
                        storeJourney(journey);

                        ServerRequest serverRequests = new ServerRequest(this);
                        serverRequests.fetchDriverDataInBackground(journey, new GetDriverCallBack() {
                            @Override
                            public void done(ArrayList<Driver> returnedDrivers) {
                                if (returnedDrivers == null || returnedDrivers.size() == 0) {
                                    displayErrorMessage("Could not fetch any drivers");
                                } else {

                                    for (int i = 0; i < returnedDrivers.size(); i++) {

                                        MarkerOptions markerOptions = new MarkerOptions();

                                        // get lat long of driver
                                        Double latitude = Double.parseDouble(String.valueOf(returnedDrivers.get(i).lat));
                                        Double longitude = Double.parseDouble(String.valueOf(returnedDrivers.get(i).posLong));
                                        LatLng position = new LatLng(latitude, longitude);

                                        markerOptions.position(position).title("Driver name: " + returnedDrivers.get(i).name).snippet("Driver rating: " + returnedDrivers.get(i).rating);

                                        BitmapDescriptor loaded_icon = BitmapDescriptorFactory
                                                .fromResource(R.drawable.car_marker);
                                        markerOptions.icon(loaded_icon);

                                        mMap.addMarker(markerOptions);
                                    }
                                }
                            }
                        });

                    } else {
                        Log.e("Error", "FinalAddress was null");
                    }

                    // Getting URL to the Google Directions API
                    String url = getDirectionsUrl(pickupLatLng, destLatLng);

                    DownloadTask downloadTask = new DownloadTask();

                    // Start downloading json data from Google Directions API
                    downloadTask.execute(url);

                    String jsonString = new String("{ \"name\": \"msime\"}");
                    try {
                        JSONObject jsonClient = new JSONObject(jsonString);
                        Intent serviceIntent = new Intent(this, TaxiAlertIntentService.class);
                        serviceIntent.putExtra("bookTrip", jsonClient.toString());
                        startService(serviceIntent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.ibMapType:
                changeMapType();
                break;

            case R.id.ibSearchDes:
                search(true);
                break;

            case R.id.ibSearchPickup:
                search(false);
                break;

            case R.id.ibHistory:
                // Create drop down to show options
                histPopupMenu = new PopupMenu(this, ibHistory);
                MenuInflater mI = histPopupMenu.getMenuInflater();
                mI.inflate(R.menu.popup_history, histPopupMenu.getMenu());

                Log.e("HERE", "Created History Popup");

                // Fetch Journey details from Server
                ServerRequest serverRequests = new ServerRequest(this);
                serverRequests.fetchJourneyDataInBackground(clientLocalStore.getLoggedInClient(), new GetJourneyCallBack() {
                    @Override
                    public void getJourneys(ArrayList<Journey> journeys) {
                        if (journeys.size() == 0 || journeys == null) {
                            displayErrorMessage("No history found!");
                        } else {
                            for (int i = 0; i < journeys.size(); i++) {
                                Log.e("Array list", String.valueOf(journeys.get(i).clientID));
                                setJourneyHistory(journeys.get(i));
                            }
                        }
                    }

                    @Override
                    public void saveJourney(Journey returnedJourney) {
                    }
                });
                break;

            case R.id.ibTime:
                showTimeDialog();
                break;

            case R.id.ibHere:
                String pickupAdr = getFullAddress(currentLocation.getLatitude(), currentLocation.getLongitude());
                etFrom.setText(pickupAdr);
                break;
            case R.id.ibDeletePickup:
                etFrom.setText("");
                break;
            case R.id.ibDeleteDest:
                etDestination.setText("");
                break;
        }
    }

    private void displayErrorMessage(String s) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        dialogBuilder.setMessage(s);
        dialogBuilder.setPositiveButton("OK", null);
        dialogBuilder.show();
    }


    private void setJourneyHistory(Journey journey) {
        String pickupLoc = getSmallAddress(journey.pickupLat, journey.pickupLong);
        String destLoc = getSmallAddress(journey.destinationLat, journey.destinationLong);

        histPopupMenu.getMenu().add(pickupLoc + " ~ " + destLoc);
        histPopupMenu.show();
    }

    private String getDirectionsUrl(LatLng pickup, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + pickup.latitude + "," + pickup.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Except downloading url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }

            // Set distance and time labels
            tvDistance.setText("Distance: " + distance);
            tvTime.setText("Duration: " + duration);
            // ("-?[\\d\\.]+")

            Pattern p = Pattern.compile("-?[\\d\\.]+");
            float distanceNum = 0, durationNum = 0;
            Matcher m = p.matcher(distance);
            while (m.find()) {
                distanceNum = Float.parseFloat(String.valueOf(m.group()));
            }
            Matcher m2 = p.matcher(duration);
            while (m2.find()) {
                durationNum = Float.parseFloat(String.valueOf(m2.group()));
            }
            float price = (float) ((distanceNum * durationNum) * 0.3);
            tvCost.setText("£" + price);

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    public void showTimeDialog() {
        FragmentManager fragmentManager = getFragmentManager();
        TimeDialogActivity timeDialogActivity = new TimeDialogActivity();
        timeDialogActivity.show(fragmentManager, "time");
    }

    public void search(Boolean flag) {
        String location;

        if (flag == true) {
            location = etDestination.getText().toString();
        } else {
            location = etFrom.getText().toString();
        }

        List<Address> addressList = null;

        if (location != null || !(location != "")) {

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList == null || addressList.size() == 0) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setMessage("Address was not found please try again!");
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            } else {
                // Fetch address
                Address address = addressList.get(0);
                if (address != null) {

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    mMap.addMarker(new MarkerOptions().position(latLng).title("You searched for here!"));
                    zoomToLocation(latLng);

                    if (flag == true) {
                        etDestination.setText(getFullAddress(latLng.latitude, latLng.longitude));
                    } else {
                        etFrom.setText(getFullAddress(latLng.latitude, latLng.longitude));
                    }
                }
            }
        }
    }

    public void changeMapType() {
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        // Map is satellite
        else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    private void storeJourney(Journey journey) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeJourneyDataInBackground(journey, new GetJourneyCallBack() {
            @Override
            public void saveJourney(Journey journey) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setMessage("Order stored");
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }

            @Override
            public void getJourneys(ArrayList<Journey> journeys) {
            }
        });
    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private String getSmallAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                StringBuilder sb = new StringBuilder();
                //Address Line
                for (int i = 0; i < 1; i++) {
                    if (addresses.get(0).getAddressLine(i) != null)
                        sb.append(addresses.get(0).getAddressLine(i)).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getFullAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                StringBuilder sb = new StringBuilder();
                int numberOfAddressLines = addresses.get(0).getMaxAddressLineIndex();
                //Address Line
                for (int i = 0; i < numberOfAddressLines; i++) {
                    if (addresses.get(0).getAddressLine(i) != null)
                        sb.append(addresses.get(0).getAddressLine(i)).append("\n");
                }
                if (addresses.get(0).getCountryName() != null) {
                    sb.append(addresses.get(0).getCountryName()).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng position = new LatLng(-34, 151);
        String title = "I'm a car!";
        String snippet = "BROOM BROOM";
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position).title(title).snippet(snippet);

        // Standard marker icon in case image is not found
        BitmapDescriptor icon = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);

        BitmapDescriptor loaded_icon = BitmapDescriptorFactory
                .fromResource(R.drawable.car_marker);
        markerOptions.icon(loaded_icon);

        mMap.addMarker(markerOptions);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

        setUpMap();
    }

    LocationManager locationManager;

    private void setUpMap() {
        // Enable MyLocation Layer of Google Map
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            // Enables location button to move to user location
            mMap.setMyLocationEnabled(true);

            // Get LocationManager object from System Service LOCATION_SERVICE
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Create a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Get the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Check provider is avaliable
            if (locationManager.isProviderEnabled(provider)) {
                // Get Current Location
                Location myLocation = getLastKnownLocation();
                if (myLocation != null) {

                    //locationManager.getLastKnownLocation(provider);

                    //set map type
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    // Get latitude of the current location
                    double latitude = myLocation.getLatitude();

                    // Get longitude of the current location
                    double longitude = myLocation.getLongitude();

                    // Create a LatLng object for the current location
                    LatLng latLng = new LatLng(latitude, longitude);

                    mMap.addMarker(new MarkerOptions().position(latLng).title("You are here!"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    zoomToLocation(latLng);

                    currentLocation = myLocation;

                } else {
                    // display error
                    Log.e("SudoUber", "Perms check failed");
                }
            } else {
                // provider not avaliable
                Log.e("SudoUber", "Provider not avaliable error");
            }

        } else {
            // Show rationale and request permission.
            Log.d("SudoUber", "Permissions not accepted");
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return null;
            }
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() > bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    // Zooms in Maps to location specified in param
    private void zoomToLocation(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(17)
                .bearing(0)
                .tilt(40)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /* Booking Form Validation Methods */
    public boolean isValidBookingDetails(String from, String destination, String when, String payment) {
        if (isValidFrom(from) && isValidDestination(destination) && isValidWhen(when) && isValidPayment(payment))
            return true;
        else return false;
    }

    public boolean isValidFrom(String fromLocation) {
        if (fromLocation.length() == 0) {
            etFrom.setError("Please specify a pickup location");
            return false;
        } else if (fromLocation.length() > 100) {
            etFrom.setError("Location can only be up to 100 characters");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidDestination(String destinationLocation) {
        if (destinationLocation.length() == 0) {
            etFrom.setError("Please specify a destination location");
            return false;
        } else if (destinationLocation.length() > 100) {
            etFrom.setError("Location can only be up to 100 characters");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidWhen(String when) {
        if (when.length() == 0) {
            tvWhen.setError("Please specify a time of pickup!");
            return false;
        } else if (when.length() > 100) {
            tvWhen.setError("Time in format DD/MM/YYYY HH:MM");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidPayment(String payment) {
        if (payment.length() == 0) {
            //tPayment.setError("Please specify a payment method!");
            return false;
        } else if (payment.length() > 100) {
            //etPayment.setError("Location can only be cash or card!");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onDialogMessage(String message, Calendar dateTime) {
        whenDate = dateTime;
        tvWhen.setTextColor(ContextCompat.getColor(this, R.color.RED));
        tvWhen.setText("Date and time requested: " + message);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // get item selected
        if (parent.getItemAtPosition(position).equals("Card")) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
            dialogBuilder.setMessage("Card Payments are not available at this time! Please use cash");
            dialogBuilder.setPositiveButton("OK", null);
            dialogBuilder.show();
            parent.setSelection(0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void drawLine(LatLng pickup, LatLng destination) {
        PolylineOptions line =
                new PolylineOptions()
                        .add(pickup, destination)
                        .width(5)
                        .color(Color.RED);
        mMap.addPolyline(line);
    }
}