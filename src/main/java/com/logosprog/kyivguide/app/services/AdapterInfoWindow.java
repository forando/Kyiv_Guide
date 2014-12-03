package com.logosprog.kyivguide.app.services;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.logosprog.kyivguide.app.R;

public class AdapterInfoWindow  implements InfoWindowAdapter {

    // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
    // "title" and "snippet".
    private final View mWindow;
    private final View mContents;
    private String b_name;

    public AdapterInfoWindow(Context context, String button_name){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        mWindow = inflater.inflate(R.layout.info_window, null);
        mContents = inflater.inflate(R.layout.info_contents, null);
        b_name = button_name;
    }

    @Override
    public View getInfoContents(Marker marker) {
        render(marker, mContents);
        return mContents;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    private void render(Marker marker, View view) {
        int badge;
        // Use the equals() method on a Marker to check for equals.  Do not use ==.
        if (b_name.equals(PlaceSearch.PLACE_SEE)) {
            badge = R.drawable.ic_bubble_search_see;
        } else if (b_name.equals(PlaceSearch.PLACE_ATTRACTIONS)) {
            badge = R.drawable.ic_bubble_search_attractions;
        } else if (b_name.equals(PlaceSearch.PLACE_SHOPPING)) {
            badge = R.drawable.ic_bubble_search_shopping;
        } else if (b_name.equals(PlaceSearch.PLACE_BEAUTY)) {
            badge = R.drawable.ic_bubble_search_beauty;

        } else if (b_name.equals(PlaceSearch.PLACE_HOTELS)) {
            badge = R.drawable.ic_bubble_search_hotels;
        } else if (b_name.equals(PlaceSearch.PLACE_CAFE)) {
            badge = R.drawable.ic_bubble_search_cafe;
        } else if (b_name.equals(PlaceSearch.PLACE_BARS)) {
            badge = R.drawable.ic_bubble_search_bars;
        } else if (b_name.equals(PlaceSearch.PLACE_RESTAURANTS)) {
            badge = R.drawable.ic_bubble_search_restaurants;

        } else if (b_name.equals(PlaceSearch.PLACE_ATM)) {
            badge = R.drawable.ic_bubble_search_atm;
        } else if (b_name.equals(PlaceSearch.PLACE_BANK)) {
            badge = R.drawable.ic_bubble_search_atm;
        } else if (b_name.equals(PlaceSearch.PLACE_AIRPORT)) {
            badge = R.drawable.ic_bubble_search_airport;
        } else if (b_name.equals(PlaceSearch.PLACE_GAS)) {
            badge = R.drawable.ic_bubble_search_gas;
        } else {
            // Passing 0 to setImageResource will clear the image view.
            badge = 0;
        }
        ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            // Spannable string allows us to edit the formatting of the text.
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
            titleUi.setText(titleText);
        } else {
            titleUi.setText("");
        }

        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null && snippet.length() > 12) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
            snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

}
