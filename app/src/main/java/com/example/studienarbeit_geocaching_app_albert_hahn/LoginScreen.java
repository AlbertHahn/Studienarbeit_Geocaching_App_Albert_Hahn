package com.example.studienarbeit_geocaching_app_albert_hahn;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.List;

/**
 * Login screen for the geocaching app with access to the class AccountCreation
 * and the actual MainActivity GeochacheMap, where all main functions are declared
 */

public class LoginScreen extends AppCompatActivity  {

    /**
     * Tag for Log.d and Error Code
     */

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    /**
     * Variables for shared prefrences remembers username and password
     */

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAME_TEXT = "text";
    public static final String USERNAME_PASSWORD = "password";

    /**
     * Buttons and viewelements
     */
    private Button mLogin, mAccountCreation;
    private EditText mUserName, mPassword;
    private String mUserNameText, mPasswordText;

    /**
     * Including the databasehelper for account creation
     */
    DataBaseHelper dataBaseHelper ;

    /**
     * Initialization of variables, database object, view elements and
     * Initialization of button listeners to access account creation and GeocacheMap
     * Loading and updating functions for sharedpreferences
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_screen);

        dataBaseHelper = new DataBaseHelper(LoginScreen.this);
        mLogin = (Button) findViewById(R.id.Maps);
        mAccountCreation = (Button) findViewById(R.id.AccountCreation);

        mUserName = (EditText) findViewById(R.id.Username);
        mPassword = (EditText) findViewById(R.id.Password);


        mLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v){
                Login();
            }
        });

        mAccountCreation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Create();
            }
        });

        loadData();
        updateViews();
    }

    /**
     * Function for login into a account by comparing the database entry of user model
     * with the given username and password by equal string matching
     */

    private void Login() {
        // Calling a database object and calling a usermodel list
        dataBaseHelper = new DataBaseHelper(LoginScreen.this);
        List <UserModel> list = dataBaseHelper.selectAllUserModel();
        // Boolean to show if username was found in the list
        boolean found = false;

        if(list!=null)
        {
            for(int i = 0; i < list.size(); i++)
            {

                // Comparing username and password with list entries
                if(list.get(i).getName().equals(mUserName.getText().toString()) && list.get(i).getPassword().equals(mPassword.getText().toString()))
                {
                    // Checking if latest google service api is installed
                    boolean Success = ServiceErrorHandling();
                    if(Success){
                        // saveData with sharedprefrences on succes and start next intent GeocacheMap
                        // and passing through the username to be able to use relevant functions to the username in GeocacheMap
                        found = true;
                        saveData();
                        Intent intentMap = new Intent(LoginScreen.this, GeocacheMap.class);
                        intentMap.putExtra("EXTRA_USER_NAME", list.get(i).getName());
                        startActivity(intentMap);
                    }
                    else{
                    }
                }
            }
            if(!found)
            {
                // Account couldn't be found, Toast!
                Toast.makeText(LoginScreen.this, "ACCOUNT COULDN'T BE FOUND", Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Starting class with intent for account creation
     */

    private void Create() {
        Intent intentAccountCreation = new Intent(LoginScreen.this, AccountCreation.class);
        startActivity(intentAccountCreation);
    }

    /**
     * Functions for shared preferences saving, loading and updating current view
     * with given preferences like username and password
     */

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USERNAME_TEXT, mUserName.getText().toString());
        editor.putString(USERNAME_PASSWORD, mPassword.getText().toString());
        editor.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        mUserNameText = sharedPreferences.getString(USERNAME_TEXT, "");
        mPasswordText = sharedPreferences.getString(USERNAME_PASSWORD, "");
    }

    public void updateViews(){
        mUserName.setText(mUserNameText);
        mPassword.setText(mPasswordText);
    }

    /**
     * used for error handling and assuring the user of the device has the latest google play service updates
     * @return boolean if successful or not
     */

    public boolean ServiceErrorHandling()
    {
        Log.d(TAG, "ServiceErrorHandling: google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(LoginScreen.this);
        if(available == ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "ServiceErrorHandling: Google Play Services are up to date!");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {
            Log.d(TAG, "ServiceErrorHandling: error can be fixed by updating");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(LoginScreen.this, available, ERROR_DIALOG_REQUEST);
        }
        else{
            Toast.makeText(this, "Won't work", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

}