package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Activity to show remaining caches in geocache table
 */

public class GeocacheListRemaining extends AppCompatActivity {

    /**
     * @value lv_geocache listview to fill in remaining geocaches
     * @value geocacheArrayAdapter adapter to translate list as listview
     * @value dataBasehelper for retrieving geocache model lists
     * @value listFalse means lists that is false because the geocaches weren't found yet
     */

    ListView lv_geocache;
    ArrayAdapter geocacheArrayAdapter;
    DataBaseHelper dataBaseHelper;
    List<String> listFalse;
    private List<Integer> iconID;

    /**
     * declaring of listview
     * @param savedInstanceState nothing has been passed with extra information
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list_remaining);

        dataBaseHelper = new DataBaseHelper(GeocacheListRemaining.this);
        lv_geocache = (ListView) findViewById(R.id.Geocache_list_remaining);

        // Function to create adapter
        ShowGeocacheOnListView();

    }

    /**
     * Shows list on listview takes the databasehelper and retrieves the geocache table as list
     * goes through the list an picks out which of them should be shown and which not
     */

    private void ShowGeocacheOnListView() {
        // Retrieve GeocacheModal list
        List<GeocacheModel> list = dataBaseHelper.selectAll();
        listFalse = new ArrayList<>();


        // go through list and get every entry with geocaches that werent found yet
        for(int i = 0; i<list.size();i++)
        {
            if(!list.get(i).found())
            {
                // add them to a new list
                listFalse.add(list.get(i).getName());
            }
        }

        // converts the list to an string Array so the custom adapter can work with the provided parameter
        String[] stringArray = listFalse.toArray(new String[0]);
        // initialize list for icon ids
        iconID = new ArrayList<Integer>();

        for (int i = 0; i < stringArray.length; i++) {
            // search through list and add all items in the list as drawable
            iconID.add(getResources().getIdentifier(stringArray[i], "drawable", this.getPackageName()));
        }

        // setup the new list as a adapter and set it to listview
        CustomAdapter customAdapter = new CustomAdapter(this, stringArray, iconID);
        lv_geocache.setAdapter(customAdapter);
    }
}
