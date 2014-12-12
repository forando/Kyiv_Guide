package com.logosprog.kyivguide.app.services.searchers;

/**
 * Created by forando on 11.12.14.<br>
 * An Abstract class that realizes Factory Method Pattern
 */
public abstract class PlaceSearcherFactory {

    /**
     * A static method that provides instance of
     * {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearcher} class
     * @param latitude Object latitude
     * @param longitude Object longitude
     * @param placeType Object Category Type or Text Query
     * @return corespondent instance of
     * {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearcher} class
     */
    public static PlaceSearcher newInstance(double latitude, double longitude, String placeType) {
        // Use the equals() method on a Marker to check for equals.  Do not use ==.
        if (placeType.equals(PlaceSearcher.PLACE_SEE)) {
            return new PlaceSeeNearbySearcher(latitude, longitude);
        } else if (placeType.equals(PlaceSearcher.PLACE_ATTRACTIONS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_SHOPPING)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BEAUTY)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_HOTELS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_CAFE)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BARS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_RESTAURANTS)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_ATM)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_BANK)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_AIRPORT)) {
            return null;
        } else if (placeType.equals(PlaceSearcher.PLACE_GAS)) {
            return null;
        } else {
            if (placeType.length()>2) {//expecting to have some text input that is more than 2 chars length
                return new PlaceTextSearcher(latitude, longitude, placeType);
            }else{
                return null;
            }
        }
    }
}
