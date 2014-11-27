package com.logosprog.kyivguide.app.fragments.delegates;

/**
 * Created by forando on 27.11.14.<br>
 * Implemented by {@link com.logosprog.kyivguide.app.fragments.Map}
 * to provide interaction with activity that uses it
 */
public interface MapDelegate {
    public void moveTo(double lat, double lon);
    public void clearMap();
}
