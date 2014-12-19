package com.logosprog.kyivguide.app.services.searchers;

import com.logosprog.kyivguide.app.utils.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * An interface to be used for {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 * list filtering.<br>
 *     Implement it by following Decorator Pattern -> ComponentInterface realisation.<br>
 * Created by forando on 16.12.14.
 */
public interface PlacesFilter extends Filter<PlaceSearchPoint> {
    /**
     * This method is implemented by concrete Filters to perform
     * specific filtering on list of
     * {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
     * @return Filterred by some criteria list of
     * {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
     */
    @Override
    ArrayList<PlaceSearchPoint> filter();
}
