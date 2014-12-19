package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * A class that holds in itself {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 * list.<br>
 *     This class is needed only for Decorator Pattern realisation.<br>
 *     According to Decorator Pattern this class represents Decorator Pattern -> ConcreteComponent.<br>
 * Created by forando on 19.12.14.
 */
public class PlacesHolder implements PlacesFilter {

    private ArrayList<PlaceSearchPoint> places;

    public PlacesHolder(ArrayList<PlaceSearchPoint> places) {
        this.places = places;
    }

    @Override
    public ArrayList<PlaceSearchPoint> filter() {
        return places;
    }

    public ArrayList<PlaceSearchPoint> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<PlaceSearchPoint> places) {
        this.places = places;
    }
}
