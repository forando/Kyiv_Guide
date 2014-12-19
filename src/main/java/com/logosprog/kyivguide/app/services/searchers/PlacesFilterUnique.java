package com.logosprog.kyivguide.app.services.searchers;

import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class that filters out all repeated {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 * objects.<br>
 *     Implements Decorator Pattern.<br>
 * Created by forando on 17.12.14.
 */
public class PlacesFilterUnique extends PlacesFilterDecorator {

    public PlacesFilterUnique(PlacesFilter placesFilter) {
        super(placesFilter);
    }

    @Override
    public ArrayList<PlaceSearchPoint> filter() {

        ArrayList<PlaceSearchPoint> placesFilterInput = super.filter();
        ArrayList<PlaceSearchPoint> placesFilterOutput = new ArrayList<PlaceSearchPoint>();

        ArrayList<Location> existingLocations = new ArrayList<Location>();

        for (PlaceSearchPoint placeInput : placesFilterInput) {
            boolean keyExist = false;
            Location locInput = new Location("input");
            locInput.setLatitude(placeInput.getLatitude());
            locInput.setLongitude(placeInput.getLongitude());
            String placeInputName = placeInput.getName();
            int i = 0;
            for (Location existingLocation : existingLocations) {
                if (existingLocation.distanceTo(locInput)<1 &&
                        placeInputName.equals(placesFilterOutput.get(i).getName())){
                    keyExist = true;
                    break;
                }
                ++i;
            }

            /*for (PlaceSearchPoint placeOutput : placesFilterOutput) {
                if (placeInputReference.equals(placeOutput.getReference())) {
                    keyExist = true;
                    break;
                }
            }*/
            if (!keyExist) {
                existingLocations.add(locInput);
                placesFilterOutput.add(placeInput);
            }
        }

        return placesFilterOutput;
    }
}
