package com.logosprog.kyivguide.app.services.searchers;

import java.util.ArrayList;

/**
 * This class implements Decorator Pattern.<br>
 *     It realises Decorator Pattern -> Decorator abstract class.<br>
 * This class implemented by concrete Filters to perform
 * specific filtering on list of
 * {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 *<br>
 *
 * Created by forando on 19.12.14.
 */
public abstract class PlacesFilterDecorator implements PlacesFilter {

    PlacesFilter placesFilter;

    public PlacesFilterDecorator(PlacesFilter placesFilter) {
        this.placesFilter = placesFilter;
    }

    @Override
    public ArrayList<PlaceSearchPoint> filter() {
        return placesFilter.filter();
    }
}
