package com.logosprog.kyivguide.app.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import com.logosprog.kyivguide.app.App;
import com.logosprog.kyivguide.app.R;
import com.logosprog.kyivguide.app.services.PlaceSearch;
import com.logosprog.kyivguide.app.services.PlacesService;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.logosprog.kyivguide.app.fragments.Search.SearchListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Search extends Fragment implements OnItemClickListener {

    Context context;

    private static final String ARG_REGION = "region";
    private static final String ARG_RADIUS = "radius";

    // TODO: Rename and change types of parameters
    private String region;
    private int radius;

    private SearchListener mListener;

    View fragment;
    AutoCompleteTextView input;
    ImageButton bGo;
    ImageButton bDelete;

    SimpleAdapter s_adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reg Region.
     * @param r Radius.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String reg, int r) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_REGION, reg);
        args.putInt(ARG_RADIUS, r);
        fragment.setArguments(args);
        return fragment;
    }
    public Search() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            region = getArguments().getString(ARG_REGION);
            radius = getArguments().getInt(ARG_RADIUS);
            context = getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (container == null) {
            return null;
        }
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_search, container, false);
        input = (AutoCompleteTextView) fragment.findViewById(R.id.autocomplete_country);
        bGo = (ImageButton) fragment.findViewById(R.id.search_go_btn);
        bDelete = (ImageButton) fragment.findViewById(R.id.search_delete_btn);

        setupInput();

        return fragment;
    }

    private void setupInput() {
        input.setThreshold(1);

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                new GetPredictions().execute(s.toString());

            }

        });

        //In landscape mode, adding listener to keyboard "SEARCH" button
        //this is only for phones and will not be necessary for tablets.
        input.setOnEditorActionListener(new EditText.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionID, KeyEvent event) {
                if (actionID == EditorInfo.IME_ACTION_SEARCH){
                    if(!input.getText().toString().equals("")){
                        mMap.clear();
                        new PlaceSearch(context, PlacesService.TEXT_SEARCH, mMap, tempLocation, input.getText().toString(), "dummy_text").execute();
                    }

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    //remove focus frome searchBar
                    findViewById(R.id.layout_map).requestFocus();

                    return true;
                }else{
                    return false;
                }
            }});

        input.setOnItemClickListener(this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // TODO: uncomment this later
            //mListener = (SearchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        HashMap<String, String> hm = (HashMap<String, String>) adapterView.getItemAtPosition(position);

        //hide keyboard
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        findViewById(R.id.layout_map).requestFocus();

        ViewGroup.LayoutParams btn_go_params = bGo.getLayoutParams();
        btn_go_params.width = 0;
        bGo.setLayoutParams(btn_go_params);

        ViewGroup.LayoutParams btn_delete_params = bDelete.getLayoutParams();
        btn_delete_params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        bDelete.setLayoutParams(btn_delete_params);

        if(!input.getText().toString().equals("")){
            mMap.clear();
            String referense = hm.get("reference");
            new PlaceDetails(context, mMap, referense, "dummy_text").execute();
            //new PlaceSearch(context, PlacesService.TEXT_SEARCH, mMap, tempLocation, input.getText().toString(), "dummy_text").execute();
        }

        //Toast.makeText(this, hm.get("description"), Toast.LENGTH_SHORT).show();
    }

    //=================================================================================
    //AsyncTask<String GetPredictions BEGIN
    //=================================================================================
    public class GetPredictions extends AsyncTask<String, Void, List<HashMap<String, String>>> {

        @Override
        protected List<HashMap<String, String>> doInBackground(String... inputs) {
            PlacesService service = new PlacesService(null);
            // ArrayList<String> predictionsList =
            // service.placeQueryAutocomplete(41.69275175761847,
            // 44.81409441679716, inputs[0]);
            List<HashMap<String, String>> predictions = service.placeAutocomplete(App.LATITUDE, App.LONGITUDE, inputs[0]);
            return predictions;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> predictions) {
            super.onPostExecute(predictions);
            if (predictions != null) {

                String[] FROM = new String[] { "description" };
                int[] TO = new int[] { R.id.text1 };

                // Creating a SimpleAdapter for the AutoCompleteTextView
                s_adapter = new SimpleAdapter(context, predictions,
                        R.layout.list_maps_row, FROM, TO);

                // Setting the adapter
                input.setAdapter(s_adapter);
                s_adapter.notifyDataSetChanged();
            }
        }

    }
    //=================================================================================
    //AsyncTask<String GetPredictions END
    //=================================================================================

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SearchListener {
        // TODO: Update argument type and name
        public void onSearch(Uri uri);
    }

}
