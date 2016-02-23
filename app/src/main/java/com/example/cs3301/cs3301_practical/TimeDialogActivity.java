package com.example.cs3301.cs3301_practical;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;


public class TimeDialogActivity extends DialogFragment implements View.OnClickListener {
    Button bCancel, bSelect;
    Communicator communicator;
    TimePicker timePicker;

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

            String timeStr = "" + timeHr + timeMin;
            communicator.onDialogMessage(timeStr);
            dismiss();
            //Toast.makeText(getActivity(), "Time selected", Toast.LENGTH_SHORT).show();


        } else {
            // Cancel selected
            String msg = "Please specify time";
            communicator.onDialogMessage(msg);
            dismiss();
            //Toast.makeText(getActivity(), "Cancel selected", Toast.LENGTH_SHORT).show();
        }
    }

    interface Communicator {
        void onDialogMessage(String message);
    }
}


