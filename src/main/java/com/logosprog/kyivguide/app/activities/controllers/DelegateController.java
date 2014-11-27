package com.logosprog.kyivguide.app.activities.controllers;

import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;

/**
 * Created by forando on 27.11.14.<br>
 * Implemented by activities who want to register fragments as delegates
 * for interaction with them.
 */
public interface DelegateController<T> {
    public static final int TYPE_MAP = 1001;
    public static final int TYPE_SEARCH = 1002;

    /**
     *
     * @param delegate The Delegate to be registered in activity
     * @param type The Delegate Type. Use ONLY
     *             {@link com.logosprog.kyivguide.app.activities.controllers.DelegateController}
     *             static integers.
     */
    public void registerDelegate(T delegate, int type);
}
