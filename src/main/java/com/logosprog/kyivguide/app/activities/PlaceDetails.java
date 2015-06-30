package com.logosprog.kyivguide.app.activities;

import java.util.ArrayList;

import android.app.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.app.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.fragments.Map;
import com.logosprog.kyivguide.app.fragments.delegates.MapDelegate;
import com.logosprog.kyivguide.app.services.DirectionsService;
import com.logosprog.kyivguide.app.services.Place;

/**
 * Created by forando on 07.12.14.
 */
public class PlaceDetails extends FragmentActivity implements Map.MapListener {

    private final String TAG = getClass().getSimpleName();

    MapDelegate mapDelegate;

    Place place;

    View detailsView;
    View mapView;
    int mapViewHeight;
    int mapViewAnimTime;

    TextView title;
    TextView address;
    TextView phone;
    ImageView image1;
    ImageView image2;
    ImageView image3;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        context = this;

        final android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        final android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        android.support.v4.app.Fragment map = Map.newInstance(App.LATITUDE, App.LONGITUDE);
        transaction.replace(R.id.place_details_frame_map, map, "map").commit();

        //initMap();

        detailsView = findViewById(R.id.fullscreen_content_details_info);
        mapView = findViewById(R.id.fullscreen_content_map);

        mapViewAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        Intent intent = getIntent();
        String reference = intent.getExtras().getString("reference");

        Log.i(TAG, "reference = " + reference);

        title = (TextView) findViewById(R.id.pd_formated_name);
        address = (TextView) findViewById(R.id.pd_formatted_address);
        //phone = (TextView) findViewById(R.id.pd_formated_phone);



		/*image1 = (ImageView) findViewById(R.id.image1);
		image2 = (ImageView) findViewById(R.id.image2);
		image3 = (ImageView) findViewById(R.id.image3);*/

        new setContent().execute(reference);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_place_details, menu);
        return true;
    }

    private void togge_view(boolean hide){
        if (hide){
            //controlsView.setVisibility(View.GONE);
            mapView.animate().translationY(mapViewHeight).setDuration(mapViewAnimTime);
            mapDelegate.clearMap();
        }else{
            mapView.animate().translationY(0).setDuration(mapViewAnimTime);
            //controlsView.setVisibility(View.VISIBLE);
            //delay_goBack(10000);
        }
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            //togge_view(true);
            mapViewHeight = mapView.getHeight();
            togge_view(true);
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delay_mapHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void b_map_hide_OnClick(View v){
        mapViewHeight = mapView.getHeight();//åñëè çàïðîñèòü ýòó âåëè÷èíó äî åž ïîÿâëåíèÿ íà ýêðàíå òî îíà áóäåò = 0
        mapView.animate().translationY(mapViewHeight).setDuration(mapViewAnimTime);
        mapDelegate.clearMap();
    }

    public void RadioButton_onClick(View v){
        RadioButton rb = (RadioButton)v;
        switch (rb.getId()) {
            case R.id.radio_driving:
                mapDelegate.getDirections(place, DirectionsService.MODE_DRIVING);
                break;
            case R.id.radio_walking:
                mapDelegate.getDirections(place, DirectionsService.MODE_WALKING);
                break;
		/*case R.id.radio_transit: new DirectionsExecutor(place, DirectionsService.MODE_TRANSIT).execute();
		    break;
		case R.id.radio_bicycling: new DirectionsExecutor(place, DirectionsService.MODE_BICYCLING).execute();
		    break;*/

            default:
                break;
        }
    }

    @Override
    public void registerMapDelegate(MapDelegate delegate) {
        mapDelegate = delegate;
    }

    @Override
    public void showMap() {
        mapView.animate().translationY(0).setDuration(mapViewAnimTime);//showing map
    }

    @Override
    public void OnMarkersAdded(int quantity) {
        //dummy
    }

    private class setContent extends AsyncTask <String, Void, Place>{

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Loading..");
            dialog.isIndeterminate();
            dialog.show();
        }

        @Override
        protected Place doInBackground(String... references) {
            place = new Place(references[0]);
            return place;
        }

        @Override
        protected void onPostExecute(Place place) {
            super.onPostExecute(place);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if(place != null){
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                title.setText(place.getName());
                LinearLayout text_wrapper = (LinearLayout)findViewById(R.id.text_wrapper);
                inflater.inflate(R.layout.address, text_wrapper);
                address = (TextView) text_wrapper.findViewById(R.id.pd_formatted_address);
                address.setText(place.getFormattedAddress());
                if(place.getInternationalPhoneNumber() != "empty"){
                    inflater.inflate(R.layout.phone, text_wrapper);
                    phone = (TextView) text_wrapper.findViewById(R.id.pd_formated_phone);
                    phone.setText(place.getInternationalPhoneNumber());
                }

                LinearLayout photo_wrapper = (LinearLayout)findViewById(R.id.photo_wrapper);

                if(place.havePhoto()){
                    ArrayList<Bitmap>bitmaps = place.getBitmapList();
                    Log.d(TAG, "BitMap Size Is: " + bitmaps.size());
                    for(int i=0; i<bitmaps.size(); i++){

                        inflater.inflate(R.layout.photo, photo_wrapper);
                        ImageView image = (ImageView) photo_wrapper.findViewById(R.id.photo);
                        image.setImageBitmap(bitmaps.get(i));
                        image.setId(image.getId() + i + 10000);

                    }
                }/*else{
					inflater.inflate(R.layout.photo, photo_wrapper);
				}*/

                text_wrapper.invalidate();
                photo_wrapper.invalidate();

                delay_mapHide(0);
            }

        }



    }
}
