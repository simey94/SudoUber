package com.example.cs3301.cs3301_practical;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class TaxiAlertIntentService extends IntentService {

    private static final String ACTION_REQUEST = "com.example.cs3301.cs3301_practical.action.REQUEST";

    public TaxiAlertIntentService() {
        super("TaxiAlertIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("HandleAction", "About to be called");
        if (intent != null) {
            final String jsonMessage = intent.getStringExtra("bookTrip");
            handleActionFoo(jsonMessage);
        }
    }

    private void handleActionFoo(String json) {
        try {
            Log.e("HandleAction", "IN HandleAction");
            JSONObject j = new JSONObject(json);
            // get clients name
            String name = j.getString("name");
            // Mock taxi arrival time 20 seconds
            Thread.sleep(10000);
            NotificationManager mNotificationManager = (NotificationManager)
                    this.getSystemService(Context.NOTIFICATION_SERVICE);

            Intent intent = new Intent(this, MainActivity.class);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                    intent, PendingIntent.FLAG_CANCEL_CURRENT);

//            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                    .setContentTitle("SudoUber Taxi Booking!")
//                    .setStyle(new NotificationCompat.BigTextStyle()
//                            .bigText("Your cab has arrived!"))
//                        .setContentText("Cab for " + name + " has arrived!").setSmallIcon(R.drawable.car_marker)
//                    .setPriority(Notification.PRIORITY_HIGH);

            // TODO: change notification to APP LOGO
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
            mBuilder.setSmallIcon(R.drawable.car_marker)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_driver_launcher))
                    .setColor(getResources().getColor(R.color.RED))
                    .setContentTitle("SudoUber Taxi Booking!")
                    .setContentIntent(contentIntent)
                    .setContentText("Cab for " + name + " has arrived!")
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH);

            mBuilder.setContentIntent(contentIntent);
            mNotificationManager.notify(1, mBuilder.build());

            Log.e("HandleAction", "END HandleAction");

        } catch (InterruptedException | JSONException e) {
            e.printStackTrace();
        }
    }
}
