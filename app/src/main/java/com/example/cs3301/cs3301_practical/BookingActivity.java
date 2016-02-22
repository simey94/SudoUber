package com.example.cs3301.cs3301_practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class BookingActivity extends Activity implements View.OnClickListener {

    EditText etFrom, etDestination, etWhen, etPayment;
    Button bRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Set popup activity to take up 80% of screen
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .8));

        // Setup  UI elements
        etFrom = (EditText) findViewById(R.id.etFrom);
        etDestination = (EditText) findViewById(R.id.etDestination);
        etWhen = (EditText) findViewById(R.id.etWhen);
        etPayment = (EditText) findViewById(R.id.etPayment);
        bRequest = (Button) findViewById(R.id.bRequest);

        // Add on click to request button
        bRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        // get form details
        String from = etFrom.getText().toString();
        String destination = etDestination.getText().toString();
        String strWhen = etWhen.getText().toString();
        int when = Integer.parseInt(strWhen);
        String payment = etPayment.getText().toString();

        // Input form validation
        if (isValidBookingDetails(from, destination, when, payment)) {
            // valid details supplied by user
            int clientID = -4;
            double currentLong = 0, currentLat = 0;

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                clientID = bundle.getInt("clientID");
                currentLat = bundle.getDouble("latitude");
                currentLong = bundle.getDouble("longitude");
            }

            String finalAddress = getFullAddress(currentLat, currentLong);
            Log.e("FINAL ADDRESS: ", finalAddress);

            if (finalAddress != null) {
                Journey journey = new Journey(finalAddress, destination, when, payment, clientID);
                storeJourney(journey);
            } else {
                // explode

            }

            // store info in journey table


            // display popup saying booking
        }
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

    private void storeJourney(Journey journey) {
        ServerRequest serverRequest = new ServerRequest(this);
        serverRequest.storeJourneyDataInBackground(journey, new GetJourneyCallBack() {
            @Override
            public void done(Journey returnedOrder) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BookingActivity.this);
                dialogBuilder.setMessage("Order stored");
                dialogBuilder.setPositiveButton("OK", null);
                dialogBuilder.show();
            }
        });
    }

    /* Form Validation Methods */
    public boolean isValidBookingDetails(String from, String destination, int when, String payment) {
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
            etDestination.setError("Please specify a destination");
            return false;
        } else if (destinationLocation.length() > 100) {
            etDestination.setError("Destination can only be up to 100 characters");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidWhen(int when) {
        if (when == 0) {
            etWhen.setError("Please specify a time of pickup!");
            return false;
        } else if (when > 100) {
            etWhen.setError("Time in format DD/MM/YYYY HH:MM");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidPayment(String payment) {
        if (payment.length() == 0) {
            etPayment.setError("Please specify a payment method!");
            return false;
        } else if (payment.length() > 100) {
            etPayment.setError("Location can only be cash or card!");
            return false;
        } else {
            return true;
        }
    }
}
