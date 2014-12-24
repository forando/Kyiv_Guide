package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * A class that queries google.places API for beauty objects.<br>
 *
 * Created by forando on 24.12.14.
 */
public class PlaceNearbySearcherBeauty extends PlaceNearbySearcher {

    private final String TAG = getClass().getSimpleName();

    private static final String PLACE_TYPE = "beauty_salon|spa";
    //private static final String PLACE_TYPE = "gym";

    /**
     * Keys for some place types that must be filtered out from list of desired places.
     */
    public static final String FILTER_KEYS = "интернет|hilton|тов";

    public PlaceNearbySearcherBeauty(double latitude, double longitude) {
        super(latitude, longitude, PLACE_TYPE);
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {
        PlacesHolder places = new PlacesHolder(super.getPlaces());
        PlacesFilter placesFilter = new PlacesFilterOut(new PlacesFilterUnique(places), FILTER_KEYS);
        return placesFilter.filter();
    }
}
