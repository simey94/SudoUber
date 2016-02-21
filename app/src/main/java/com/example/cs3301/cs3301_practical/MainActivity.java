package com.example.cs3301.cs3301_practical;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    Button bLogout;
    EditText etName, etAge, etUsername;
    ClientLocalStore clientLocalStore;
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        bLogout = (Button) findViewById(R.id.bLogout);

        bLogout.setOnClickListener(this);

        clientLocalStore = new ClientLocalStore(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(authenticate()){
            displayClientDetails();
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }

    // tells if user is logged in or out
    private boolean authenticate(){
        // returns true if user is logged in
        return clientLocalStore.getClientLoggedin();
    }

    private void displayClientDetails(){
        Client client = clientLocalStore.getLoggedInClient();

        etUsername.setText(client.username);
        etName.setText(client.name);
        etAge.setText(client.age + "");
    }

    @Override
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bLogout:
                clientLocalStore.clearClientData();
                clientLocalStore.setClientLoggedIn(false);


                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

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
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

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
            if(locationManager.isProviderEnabled(provider)){
                // Get Current Location
                Location myLocation =  getLastKnownLocation();
                //locationManager.getLastKnownLocation(provider);

                //set map type
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Get latitude of the current location
                double latitude = myLocation.getLatitude();

                // Get longitude of the current location
                double longitude = myLocation.getLongitude();

                // Create a LatLng object for the current location
                LatLng latLng = new LatLng(latitude, longitude);

                // Show the current location in Google Map
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

                // Zoom in the Google Map
                mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("You are here!"));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            }
            else {
                // provider not avaliable
                Log.e("SudoUber", "Provider not avaliable error");
            }

        } else {
            // Show rationale and request permission.
            Log.d("SudoUber", "Permissions not accepted");
        }
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }


    public void onSearch(View view){
        EditText location_tf = (EditText)findViewById(R.id.TFaddress);
        String location = location_tf.getText().toString();

        List<Address> addressList = null;

        if(location != null || !(location != "")){

            Geocoder geocoder = new Geocoder(this);
            try {
                addressList =  geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Fetch address
            Address address = addressList.get(0);
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng).title("You searched for here!"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(20));

        }
    }

    public void changeType(View view){

        if(mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        // Map is satellite
        else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    // When user clicks book taxi button
    public void onClickBookTaxi(View view){
        // Create drop down to show options
//        PopupMenu popupMenu = new PopupMenu(this, view);
//        MenuInflater menuInflater = popupMenu.getMenuInflater();
//        menuInflater.inflate(R.menu.popup_actions, popupMenu.getMenu());
//        PopUpMenuEventHandler popUpMenuEventHandler = new PopUpMenuEventHandler(getApplicationContext());
//        popupMenu.setOnMenuItemClickListener(popUpMenuEventHandler);
//        popupMenu.show();

       startActivity(new Intent(MainActivity.this, BookingActivity.class));

    }

}
