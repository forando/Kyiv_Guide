package com.logosprog.kyivguide.app.utils;

import java.util.ArrayList;

/**
 * Interface to be implemented by all filters.<br>
 *     Implement it by following Decorator Pattern.<br>
 * Created by forando on 16.12.14.
 */
public interface Filter<T> {

    public ArrayList<T> doFilter();
    
}
