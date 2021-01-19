package com.example.studienarbeit_geocaching_app_albert_hahn;

/**
 * Class GeocacheModel for sql queries and table data
 * includes all necessary parameter to define a geocache
 */

public class GeocacheModel {

    /**
     * @value _id unique identifier of a geocache
     * @value name which object is hidden as geocache
     * @value latitude location of the geocache
     * @value longitude location of the geocache
     * @value found status of the geocache
     * @value UserName which user has retrieved or placed the geocache
     */

    private int _id;
    private String name;
    private double latitude;
    private double longitude;
    private boolean found;
    private String UserName;

    /**
     * constructer of the geocache model
     * @param _id unique identifier of a geocache
     * @param name which object is hidden as geocache
     * @param latitude location of the geocache
     * @param longitude location of the geocache
     * @param found status of the geocache
     * @param UserName which user has retrieved or placed the geocache
     */
    public GeocacheModel(int _id, String name, double latitude, double longitude, boolean found, String UserName) {
        this._id = _id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.found = found;
        this.UserName = UserName;
    }

    /**
     * important to display the whole model as a string
     * @return string of geocachemodel
     */
    @Override
    public String toString() {
        return "GeocacheModel{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", found=" + found +
                ", UserName=" + UserName +
                '}';
    }


    /**
     * getter of the geocachemodel
     */

    public int get_id() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public double getlatitude() {
        return latitude;
    }

    public double getlongitude() {
        return longitude;
    }

    public boolean found() {
        return found;
    }

    public String getUserName() {
        return UserName;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * setter of the geocachemodel
     */

    public void setlatitude(double latitude) { this.latitude = latitude; }

    public void setlongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public void setUsername(String UserName) {
        this.UserName = UserName;
    }

}
