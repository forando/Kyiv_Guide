package com.logosprog.kyivguide.app.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.WindowManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.Map;

/**
 * Created by forando on 26.11.14.
 */
public class Maps extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        final FragmentManager manager = getFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();
        Fragment map = Map.newInstance(App.LATITUDE, App.LONGITUDE);
        transaction.replace(R.id.frame_map, map, "map");
        transaction.commit();
    }
}
