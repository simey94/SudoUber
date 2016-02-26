package com.example.cs3301.cs3301_practical;

import java.util.ArrayList;

/**
 * Created by michaelsime on 22/02/16.
 */
public interface GetJourneyCallBack {
    void saveJourney(Journey journey);

    void getJourneys(ArrayList<Journey> journeys);

}
