package com.logosprog.kyivguide.app.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.activities.controllers.DelegateController;
import com.logosprog.kyivguide.app.fragments.Map;
import com.logosprog.kyivguide.app.fragments.Search;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;

/**
 * Created by forando on 26.11.14.
 */
public class Maps extends Activity implements Map.MapListener, Search.SearchListener {

    RelativeLayout main_layout;

    MapDelegate mapDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        main_layout = (RelativeLayout)findViewById(R.id.layout_maps);

        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment map = Map.newInstance(App.LATITUDE, App.LONGITUDE);
        Fragment search = Search.newInstance("kyiv", 15);
        transaction.replace(R.id.frame_map, map, "map");
        transaction.replace(R.id.frame_search, search, "search");
        transaction.commit();
    }

    @Override
    public void onSearch(Uri uri) {
        main_layout.requestFocus();
        mapDelegate.clearMap();
    }

    @Override
    public void registerMapDelegate(MapDelegate delegate) {
        mapDelegate = delegate;
    }
}
