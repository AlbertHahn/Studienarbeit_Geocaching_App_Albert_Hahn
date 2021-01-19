package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extended fragment class to place items onto the map
 * with a given list of available options from the GeocacheMap Activity
 * bundled as an instance and passed through
 */

public class PlaceItemFragment extends Fragment {

    /**
     * @value lv_geocache listview to fill in remaining geocaches
     * @value geocacheArrayAdapter adapter to translate list as listview
     * @value dataBasehelper for retrieving geocache model lists
     * @value fastSelectList means lists that is false because the geocaches weren't found yet
     * @value geocacheModel data model for list
     * @value context to GeocacheMap
     */

    ListView lv_geocache;
    ArrayAdapter geocacheArrayAdapter;
    DataBaseHelper dataBaseHelper;
    List<String> fastSelectList;
    GeocacheModel geocacheModel;
    private Context mContext;
    private List<Integer> iconID;
    /**
     * onCreateView displays a listview with available options to choose from
     * with click on the list a Geocache will be spawned on the position of its user
     * @return view the listview as inflater inflate object
     */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initialize data helper object
        dataBaseHelper = new DataBaseHelper(mContext);
        // create view inflater with fragment loayut
        View view = inflater.inflate(R.layout.place_item_fragment, container,false);
        // get listview
        lv_geocache = (ListView) view.findViewById(R.id.Geocache_List_Fast_Select);
        // get arguments from bundle passed of GeocacheMap activity
        final String[] mDrawableName =this.getArguments().getStringArray("DrawablesNames");
        final String UserName = this.getArguments().getString("EXTRA_USER_NAME");
        // Function to create adapter
        ShowGeocacheOnListView(mDrawableName);

        // listview with onclicklistener
        lv_geocache.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // check if permission are true
                ((GeocacheMap) getActivity()).checkPermission();
                // check if gps is activated
                boolean success = ((GeocacheMap) getActivity()).getLocation();
                if(success) {
                    // get location
                    String provider = LocationManager.GPS_PROVIDER;
                    Location location = ((GeocacheMap) getActivity()).locationManager.getLastKnownLocation(provider);
                    // new data geocachemodel
                    geocacheModel = new GeocacheModel(-1, mDrawableName[position], location.getLatitude(), location.getLongitude(), false, UserName);
                    // add to geocache table
                    dataBaseHelper.AddOne(geocacheModel);
                    // update the UI after adding the geocache to the table
                    ((GeocacheMap) getActivity()).updateUI(UserName);
                }
                else{
                    Toast.makeText(getActivity(),"unsuccessful", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return view;

    }



    private void ShowGeocacheOnListView(String[] stringArray) {
        // Initialize ID list for icons
        iconID = new ArrayList<Integer>();

        for (int i = 0; i < stringArray.length; i++) {
            // search through list and add all items in the list as drawable
            iconID.add(getResources().getIdentifier(stringArray[i], "drawable", getActivity().getPackageName()));
            }

        // setup the new list with an custom adapter and set it to listview
        CustomAdapter customAdapter = new CustomAdapter(getActivity(), stringArray, iconID);
        lv_geocache.setAdapter(customAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * @param context get activity context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * delete activity context
     */

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }



}
