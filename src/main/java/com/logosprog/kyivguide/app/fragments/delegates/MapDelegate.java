package com.logosprog.kyivguide.app.fragments.delegates;

import android.location.Location;
import com.logosprog.kyivguide.app.services.Place;

/**
 * Created by forando on 27.11.14.<br>
 * Implemented by {@link com.logosprog.kyivguide.app.fragments.Map}
 * to provide interaction with activity that uses it
 */
public interface MapDelegate {
    public void moveTo(double lat, double lon);
    public void clearMap();

    /**
     * Provides objects as a search result for google.places Reference string.
     * @param reference A google.places Reference string.
     */
    public void searchReference(String reference);

    /**
     * Provides objects as a search result for a string, given by user.
     * @param text Any user input search criteria.
     */
    public void searchText(String text);

    /**
     * Provides objects of a specified type.
     * @param placeType One of a google.place specified place types see google docs.
     */
    public void searchNearBy(String placeType);

    /**
     * Provides polyline to show the way to get to the destination point.
     * @param place Destination point
     * @param mode A way to get to destination point.<br/>
     *             can be:
     *             <ul>
     *             <li>{@link com.logosprog.kyivguide.app.services.DirectionsService#MODE_BICYCLING}</li>
     *             <li>{@link com.logosprog.kyivguide.app.services.DirectionsService#MODE_DRIVING}</li>
     *             <li>{@link com.logosprog.kyivguide.app.services.DirectionsService#MODE_TRANSIT}</li>
     *             <li>{@link com.logosprog.kyivguide.app.services.DirectionsService#MODE_WALKING}</li>
     *             </ul>
     */
    public void getDirections(Place place, String mode);
}
