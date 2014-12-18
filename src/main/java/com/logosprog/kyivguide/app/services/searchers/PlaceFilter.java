package com.logosprog.kyivguide.app.services.searchers;

import com.logosprog.kyivguide.app.utils.Filter;

import java.util.List;

/**
 * An interface to be used for {@link com.logosprog.kyivguide.app.services.searchers.PlaceSearchPoint}
 * list filtering.<br>
 *     Implement it by following Decorator Pattern.<br>
 * Created by forando on 16.12.14.
 */
public interface PlaceFilter extends Filter<PlaceSearchPoint> {
}
