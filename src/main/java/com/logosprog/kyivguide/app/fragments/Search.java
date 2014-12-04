package com.logosprog.kyivguide.app.fragments;

import android.app.Activity;
import android.content.Context;
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
import com.logosprog.kyivguide.app.services.PlacesService;

import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.SearchListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Search extends Fragment implements OnItemClickListener {

    Context activityContext;

    private static final String ARG_REGION = "region";
    private static final String ARG_RADIUS = "radius";

    private String region;
    private int radius;

    private SearchListener mListener;

    View fragment;
    LinearLayout fragmentContainer;
    /**
     * The input field for user to fill in with some searching objects
     */
    AutoCompleteTextView input;
    ImageButton bGo;
    ImageButton bDelete;

    SimpleAdapter s_adapter;
    //ArrayAdapter<String> adapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param reg Region.
     * @param r Radius.
     * @return A new instance of fragment Search.
     */
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
            activityContext = getActivity();
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
        fragmentContainer = (LinearLayout) fragment.findViewById(R.id.search_edit_frame);
        input = (AutoCompleteTextView) fragment.findViewById(R.id.autocomplete_search);

        /*String[] countries = getResources().getStringArray(
                R.array.countries_array);

		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,countries);

        input.setAdapter(adapter);*/

        bGo = (ImageButton) fragment.findViewById(R.id.search_go_btn);
        bDelete = (ImageButton) fragment.findViewById(R.id.search_delete_btn);

        bGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bGo_OnClick();
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bDelete_OnClick();
            }
        });

        setupInput();

        return fragment;
    }

    private void setupInput() {
        input.setThreshold(1);

        input.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // dummy

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // dummy

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

                    fragmentContainer.requestFocus();

                    if(!input.getText().toString().equals("")){
                        mListener.onSearchText(input.getText().toString());
                    }

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

                    return true;
                }else{
                    return false;
                }
            }});

        input.setOnItemClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (SearchListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SearchListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void bGo_OnClick() {
        if(!input.getText().toString().equals("")){
            mListener.onSearchText(input.getText().toString());
            //hide keyboard
            InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

            //remove focus frome searchBar
            fragmentContainer.requestFocus();

            ViewGroup.LayoutParams btn_delete_params = bDelete.getLayoutParams();
            btn_delete_params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            bDelete.setLayoutParams(btn_delete_params);

            ViewGroup.LayoutParams btn_go_params = bGo.getLayoutParams();
            btn_go_params.width = 0;
            bGo.setLayoutParams(btn_go_params);
        }
    }

    private void bDelete_OnClick() {
        input.setText("");

        ViewGroup.LayoutParams btn_delete_params = bDelete.getLayoutParams();
        btn_delete_params.width = 0;
        bDelete.setLayoutParams(btn_delete_params);

        ViewGroup.LayoutParams btn_go_params = bGo.getLayoutParams();
        btn_go_params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        bGo.setLayoutParams(btn_go_params);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


        HashMap<String, String> hm = (HashMap<String, String>) adapterView.getItemAtPosition(position);

        //hide keyboard
        fragmentContainer.requestFocus();

        InputMethodManager imm = (InputMethodManager) activityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);

        ViewGroup.LayoutParams btn_go_params = bGo.getLayoutParams();
        btn_go_params.width = 0;
        bGo.setLayoutParams(btn_go_params);

        ViewGroup.LayoutParams btn_delete_params = bDelete.getLayoutParams();
        btn_delete_params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        bDelete.setLayoutParams(btn_delete_params);

        if(!input.getText().toString().equals("")){
            String referense = hm.get("reference");
            mListener.onReference(referense);
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
                s_adapter = new SimpleAdapter(getActivity(), predictions,
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
        /**
         * Notifies whenever user has requested to search some objects defined in
         * {@link Search#input}.
         */
        public void onSearch();

        /**
         * Notifies when a new reference has been requested.
         * @param reference Specific google.places ID that must be used in google.places request to return
         *                  desired results.
         */
        public void onReference(String reference);

        /**
         * Notifies when a nex text search has been requested
         * @param text Searched objects described in text form
         */
        public void onSearchText(String text);
    }
}
