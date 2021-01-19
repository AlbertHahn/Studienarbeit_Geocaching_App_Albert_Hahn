package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Class Activity to show all caches that were collected by the user
 */


public class GeocacheList extends AppCompatActivity {

    /**
     * @value lv_geocache listview to fill in remaining geocaches
     * @value geocacheArrayAdapter adapter to translate list as listview
     * @value dataBasehelper for retrieving geocache model lists
     * @value listTrue means lists that is false because the geocaches weren't found yet
     */

    ListView lv_geocache;
    ArrayAdapter geocacheArrayAdapter;
    DataBaseHelper dataBaseHelper;
    List<String> listTrue;
    private List<Integer> iconID;

    /**
     * declaring of listview
     * @param savedInstanceState retrieves getStringExtra with the username
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_list);
        final String UserName = getIntent().getStringExtra("EXTRA_USER_NAME");

        dataBaseHelper = new DataBaseHelper(GeocacheList.this);
        lv_geocache = (ListView) findViewById(R.id.Geocache_list);

        // Function to create adapter
        ShowGeocacheOnListView(UserName);

    }

    /**
     * Shows list on listview takes the databasehelper and retrieves the geocache table as list
     * goes through the list an picks out which of them should be shown and which not
     * @param UserName to show specific rows just to that user
     */

    private void ShowGeocacheOnListView(String UserName) {

        // Retrieve GeocacheModal list
        List<GeocacheModel> list = dataBaseHelper.selectAll();
        listTrue = new ArrayList<>();

        // If Geocache was found by User put it into the list that will be used for the Arrray Adapter
        for(int i = 0; i<list.size();i++)
        {
            // If geocache was found and
            if(list.get(i).found()&&list.get(i).getUserName().equals(UserName))
            {
                listTrue.add(list.get(i).getName());
            }
        }

        // converts the list to an string Array so the custom adapter can work with the provided parameter
        String[] stringArray = listTrue.toArray(new String[0]);
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
