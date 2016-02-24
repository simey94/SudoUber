package com.example.cs3301.cs3301_practical;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;


public class TimeDialogActivity extends DialogFragment implements View.OnClickListener {
    Button bCancel, bSelect;
    Communicator communicator;
    TimePicker timePicker;
    Calendar dateAndTime = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        communicator = (Communicator) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.time_dialog, null);
        bCancel = (Button) view.findViewById(R.id.bCancel);
        bSelect = (Button) view.findViewById(R.id.bSelect);
        timePicker = (TimePicker) view.findViewById(R.id.timePicker);

        bCancel.setOnClickListener(this);
        bSelect.setOnClickListener(this);

        setCancelable(false);
        return view;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.bSelect) {

            int timeHr = timePicker.getCurrentHour();
            int timeMin = timePicker.getCurrentMinute();

            int year = dateAndTime.get(Calendar.YEAR);
            int month = dateAndTime.get(Calendar.MONTH);
            int day = dateAndTime.get(Calendar.DAY_OF_MONTH);

            String timeStr = "" + year + ", " + month + ", " + day + ", " + timeHr + ":" + timeMin;
            communicator.onDialogMessage(timeStr, dateAndTime);
            dismiss();
            //Toast.makeText(getActivity(), "Time selected", Toast.LENGTH_SHORT).show();


        } else {
            // Cancel selected
            String msg = "Please specify time";
            communicator.onDialogMessage(msg, dateAndTime);
            dismiss();
            //Toast.makeText(getActivity(), "Cancel selected", Toast.LENGTH_SHORT).show();
        }
    }

    interface Communicator {
        void onDialogMessage(String message, Calendar dateAndTime);
    }
}


