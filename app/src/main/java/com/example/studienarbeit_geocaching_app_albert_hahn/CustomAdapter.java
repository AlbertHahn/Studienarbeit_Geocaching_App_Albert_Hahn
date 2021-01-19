package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom Adapter extension of the ArrayAdapter to inflate the listview with images
 * by setting up a xml layout file defined for rows and inserting them into the listview rows
 */

public class CustomAdapter extends ArrayAdapter {

    private String[] Name;
    private List<Integer> imgid;
    private Activity context;

    /**
     * constructor of the custom adapter
     * @param context current state of the application
     * @param Name string array names to link with pictures
     * @param imgid id of the drawable that should be applied on the imageview
     */

    public CustomAdapter(Activity context, String[] Name, List<Integer> imgid) {
        super(context, R.layout.row_entry, Name);
        this.context = context;
        this.Name = Name;
        this.imgid = imgid;
    }

    /**
     * Method to get the current view and inflate it with the new row_entry for listview
     * @param position at which position in the listview should it be changed
     * @param convertView uses the view to inflate the listview with the specified row
     * @param parent porvided to inflate your view
     * @return
     */

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null){
            row = inflater.inflate(R.layout.row_entry, null, true);
        }


        TextView listName = (TextView) row.findViewById(R.id.ListName);
        ImageView listIcon = (ImageView) row.findViewById(R.id.ListIcon);

        listName.setText(Name[position].toUpperCase());
        listIcon.setImageResource(imgid.get(position));
        return  row;
    }
}