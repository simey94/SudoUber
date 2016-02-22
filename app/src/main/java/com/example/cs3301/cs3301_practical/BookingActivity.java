package com.example.cs3301.cs3301_practical;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


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
        String when = etWhen.getText().toString();
        String payment = etPayment.getText().toString();

        // Input form validation
        if (isValidBookingDetails(from, destination, when, payment)) {
            // valid details supplied by user


            // store info in journey table


            // display popup saying booking
        }
    }


    /* Form Validation Methods */
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
            etDestination.setError("Please specify a destination");
            return false;
        } else if (destinationLocation.length() > 100) {
            etDestination.setError("Destination can only be up to 100 characters");
            return false;
        } else {
            return true;
        }
    }

    public boolean isValidWhen(String when) {
        if (when.length() == 0) {
            etWhen.setError("Please specify a time of pickup!");
            return false;
        } else if (when.length() > 100) {
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
