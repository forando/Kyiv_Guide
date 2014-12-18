package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that filters out all repeated {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 * objects.<br>
 *     Implements Decorator Pattern.<br>
 * Created by forando on 17.12.14.
 */
public class FilterUniquePlaces implements PlaceFilter {

    private ArrayList<PlaceSearchPoint> places;

    public FilterUniquePlaces(ArrayList<PlaceSearchPoint> places) {
        this.places = places;
    }

    @Override
    public ArrayList<PlaceSearchPoint> doFilter() {
        return places;
    }

    public List<PlaceSearchPoint> getPlaces() {
        return places;
    }

    public void setPlaces(ArrayList<PlaceSearchPoint> places) {
        this.places = places;
    }
}
