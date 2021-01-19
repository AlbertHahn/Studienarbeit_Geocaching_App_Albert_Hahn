package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * DataBasehelper class to extend functions of SQLiterOpenHelper
 * helps with creating tables, inserting and updating the table
 * First half of the class made with functions for the GeocacheModel
 * Second half of the class made with functions for the UserModel
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    /**
     * Database version and name
     */

    private static int DATABASE_VERSION = 1;
    private static String DATABASE_NAME = "Geocache.db";

    /**
     * Database variables for GeocacheModel and table itself
     * variables for each column
     */

    private static String GEOCACHE_TABLE = "GEOCACHE_TABLE";
    private static String COLUMN_ID = "ID " ;
    private static String COLUMN_GEOCACHE_NAME = "GEOCACHE_NAME";
    private static String COLUMN_GEOCACHE_LATITUDE = "GEOCACHE_LATITUDE " ;
    private static String COLUMN_GEOCACHE_LONGITUDE = "GEOCACHE_LONGITUDE" ;
    private static String COLUMN_GEOCACHE_FOUND = "GEOCACHE_FOUND " ;
    private static String COLUMN_GEOCACHE_USERNAME = "COLUMN_GEOCACHE_USERNAME " ;

    /**
     * Database variables for GeocacheModel and table itself
     * variables for each column
     */

    private static String USER_TABLE = "USER_TABLE";
    private static String COLUMN_USER_ID = "ID " ;
    private static String COLUMN_USER_NAME = "USER_NAME " ;
    private static String COLUMN_USER_PASSWORD = "USER_PASSWORD " ;
    private static String COLUMN_USER_LEVEL= "USER_LEVEL " ;
    private static String COLUMN_USER_EXPERIENCE= "USER_EXPERIENCE " ;

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * By accessing the database onCreate will be called and Tables created
     * Statements for GEOCACHE_TABLE as well as for USER_TABLE
     * @param db for the execution of SQL queries
     */

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create table for geocache data and inkrement id when inserting new row
        String createTableStatement = "CREATE TABLE " + GEOCACHE_TABLE +
                " (" +COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + COLUMN_GEOCACHE_NAME + " TEXT, "
                + COLUMN_GEOCACHE_LATITUDE + " DOUBLE, " + COLUMN_GEOCACHE_LONGITUDE + " DOUBLE, "
                + COLUMN_GEOCACHE_FOUND + " BOOL," + COLUMN_GEOCACHE_USERNAME + " TEXT)";
        // Create table for user data and inkrement id when inserting new row
        String createUserTableStatement = "CREATE TABLE " + USER_TABLE +
                " (" +COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "  + COLUMN_USER_NAME + " TEXT, "
                + COLUMN_USER_PASSWORD + " TEXT, " + COLUMN_USER_LEVEL + " INTEGER, " + COLUMN_USER_EXPERIENCE + " INTEGER)";
        // Execute statements for table creation
        db.execSQL(createTableStatement);
        db.execSQL(createUserTableStatement);
    }

    // this is called when the database version number changes. It prevents previous users apps form breaking when you change the database design.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Add one entry to the geocachemodel
     * @param geocacheModel accessed with getters to set new variables into the right column of table
     * @return boolean if function call was successful
     */

    public boolean AddOne(GeocacheModel geocacheModel)
    {
        /**
         * get geocachemodel data and add a row to the database
         */

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_GEOCACHE_NAME, geocacheModel.getName());
        cv.put(COLUMN_GEOCACHE_LATITUDE, geocacheModel.getlatitude());
        cv.put(COLUMN_GEOCACHE_LONGITUDE, geocacheModel.getlongitude());
        cv.put(COLUMN_GEOCACHE_FOUND, geocacheModel.found());
        cv.put(COLUMN_GEOCACHE_USERNAME, geocacheModel.getUserName());

        // Insert row to table
        long insert = db.insert(GEOCACHE_TABLE, null , cv);

        // Return if successful or not
        if(insert == -1){
            return false;
        }
        else {
            return true;
        }

    }

    /**
     * Update database row at found column and username column
     * @param geocacheModel accessed with getters to set new variables into the right column of table
     * @param UserName who should be set into the column for username
     * @return boolean if function call was successful
     */

    public boolean GeocacheWasFound(GeocacheModel geocacheModel, String UserName )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // Put the passed parameters into content values
        cv.put(COLUMN_GEOCACHE_NAME, geocacheModel.getName());
        cv.put(COLUMN_GEOCACHE_LATITUDE, geocacheModel.getlatitude());
        cv.put(COLUMN_GEOCACHE_LONGITUDE, geocacheModel.getlongitude());
        cv.put(COLUMN_GEOCACHE_FOUND, true);
        cv.put(COLUMN_GEOCACHE_USERNAME, UserName);
        // Update the content values into the geocache table
        db.update(GEOCACHE_TABLE, cv, "ID = ?", new String[]{String.valueOf(geocacheModel.get_id())});
        db.close();
        return true;
    }

    /**
     * Select all rows in table and return them as a list
     * @return list in form of GeocacheModel
     */

    public List<GeocacheModel> selectAll(){

        List<GeocacheModel> returnList = new ArrayList<>();
        // get data from the database
        String queryString = "SELECT * FROM " + GEOCACHE_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);

        if(cursor.moveToFirst())
        {
            //loop through the cursor (result set) and create new cache objects. Put them in the return list.
            //loops as long their are lines to loop through
            do{
                int geocacheID = cursor.getInt(0);
                String geocacheName = cursor.getString(1);
                double geocachelatitude = cursor.getDouble(2);
                double geocachelongitude = cursor.getDouble(3);
                boolean found = cursor.getInt(4) == 1 ? true:false; // Java Ternary Operator, like an if statement
                String geocacheUserName = cursor.getString(5);

                GeocacheModel newGeocache = new GeocacheModel(geocacheID, geocacheName, geocachelatitude, geocachelongitude , found, geocacheUserName);
                returnList.add(newGeocache);

            }while(cursor.moveToNext());

        }
        else{
            // do not add to the list
        }

        //Cleaning up and close the database and cursor
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * Select all rows in table and return them as a list
     * @return list in form of UserModel
     */

    public List<UserModel> selectAllUserModel(){
        List<UserModel> returnList = new ArrayList<>();

        // get data from the database

        String queryStringUser = "SELECT * FROM " + USER_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryStringUser, null);

        if(cursor.moveToFirst())
        {
            //loop through the cursor (result set) and create new cache objects. Put them in the return list.
            //loops as long their are lines to loop through
            do{
                int UserID = cursor.getInt(0);
                String UserName = cursor.getString(1);
                String Password = cursor.getString(2);
                int UserLevel = cursor.getInt(3);
                int UserExperience = cursor.getInt(4);

                UserModel newuserModel = new UserModel(UserID, UserName, Password , UserLevel, UserExperience);
                returnList.add(newuserModel);

            }while(cursor.moveToNext());

        }
        else{
            // do not add to the list
        }

        //Cleaning up and close the database and cursor
        cursor.close();
        db.close();
        return returnList;
    }

    /**
     * Method to update the values in the columns, Level and Experience of the user table
     * @param userModel as a reference to know which column should be updated
     * @return boolean if successful or not
     */

    public boolean LevelUpUser(UserModel userModel)
    {
        // Experience that will be gathered
        int experienceGained = 25;
        SQLiteDatabase db = this.getWritableDatabase();

        // New variables that will be update into the right row
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USER_NAME, userModel.getName());
        cv.put(COLUMN_USER_PASSWORD, userModel.getPassword());

        // Level up everytime the player hits his fourth geocache
        // 4 Geocaches are 25 exp each so on every fourth triggered geofence
        // Level will be added plus one
        if(userModel.getExperience()==75) {
            cv.put(COLUMN_USER_LEVEL, userModel.getLevel()+1);
            cv.put(COLUMN_USER_EXPERIENCE, userModel.getExperience() - 75);
            db.update(USER_TABLE, cv, "ID = ?", new String[]{String.valueOf(userModel.getid())});
            db.close();
            return true;
        }
        // if not at his fourth geocache the experience will just be increased
        else {
            cv.put(COLUMN_USER_LEVEL, userModel.getLevel());
            cv.put(COLUMN_USER_EXPERIENCE, userModel.getExperience() + experienceGained);
            db.update(USER_TABLE, cv, "ID = ?", new String[]{String.valueOf(userModel.getid())});
            db.close();
            return true;
        }
    }

    /**
     * Add a new user (row) to the user table
     * if user name isn't already used by someone else
     * @param userModel to pass data through that will be needed for the creation of a new user
     * @return boolean if successful or not
     */

    public boolean AddOneUser(UserModel userModel)
    {
        // Select list to have a comparison if username is already used or not
        List <UserModel> list = selectAllUserModel();
        Log.d("DataBaseHelper", list.toString());

        // First account?
        if(list !=null){

            // Loop through list and look if user already exists
            for(int i = 0; i< list.size(); i++)
            {
                if(list.get(i).getName().equals(userModel.getName())){
                    // User already exists return false
                    Log.d("DataBaseHelper", "User already exists");
                    return false;
                }
            }

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_USER_NAME, userModel.getName());
            cv.put(COLUMN_USER_PASSWORD, userModel.getPassword());
            cv.put(COLUMN_USER_LEVEL, 1);
            cv.put(COLUMN_USER_EXPERIENCE, 0);

            // Insert new row to user table
            long insert = db.insert(USER_TABLE, null , cv);

            if(insert == -1){
                return false;
            }
            else {
                return true;
            }
        }
        // If yes, create first user with passed userModel data
        else{

            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(COLUMN_USER_NAME, userModel.getName());
            cv.put(COLUMN_USER_PASSWORD, userModel.getPassword());
            cv.put(COLUMN_USER_LEVEL, 1);
            cv.put(COLUMN_USER_EXPERIENCE, 0);

            // Insert new row to user table
            long insert = db.insert(USER_TABLE, null , cv);

            if(insert == -1){
                return false;
            }
            else {
                return true;
            }
        }
    }
}
