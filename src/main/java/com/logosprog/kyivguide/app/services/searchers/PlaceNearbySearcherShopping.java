package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * A class that queries google.places API for shopping objects.<br>
 *
 * Created by forando on 24.12.14.
 */
public class PlaceNearbySearcherShopping extends PlaceNearbySearcher {

    private final String TAG = getClass().getSimpleName();

    private static final String PLACE_TYPE = "shopping_mall|jewelry_store";

    /**
     * Keys for some place types that must be filtered out from list of desired places.
     */
    public static final String FILTER_KEYS = "рем|fountain|тов";

    public PlaceNearbySearcherShopping(double latitude, double longitude) {
        super(latitude, longitude, PLACE_TYPE);
    }

    @Override
    public ArrayList<PlaceSearchPoint> getPlaces() {
        PlacesHolder places = new PlacesHolder(super.getPlaces());
        PlacesFilter placesFilter = new PlacesFilterOut(new PlacesFilterUnique(places), FILTER_KEYS);
        return placesFilter.filter();
    }
}
