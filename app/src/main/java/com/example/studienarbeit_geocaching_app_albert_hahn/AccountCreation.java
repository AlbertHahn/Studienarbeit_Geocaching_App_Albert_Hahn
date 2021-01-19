package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * class to create a account and insert the user to the usermodel
 * with the help of DataBaseHelper
 */

public class AccountCreation extends AppCompatActivity {

    /**
     * Buttons and viewelements
     */

    private Button Create, Back;
    private EditText SetUserName, SetPassword;

    /**
     * Datamodel and database objects
     */

    UserModel userModel;
    DataBaseHelper dataBaseHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);

        /**
         * initialize buttons, view elements, button listeners and databasehelper object
         */

        dataBaseHelper = new DataBaseHelper(AccountCreation.this);
        Create = (Button) findViewById(R.id.CreateAccount);
        Back = (Button) findViewById(R.id.BackToLogin);
        SetUserName = (EditText) findViewById(R.id.CreateUsername);
        SetPassword = (EditText) findViewById(R.id.CreatePassword);

        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // look if edittext fields are empty
                if(!SetUserName.getText().toString().isEmpty() && !SetPassword.getText().toString().isEmpty())
                {
                    // if not Add user to db table
                    userModel = new UserModel(-1, SetUserName.getText().toString(),SetPassword.getText().toString(), 1 , 0);
                    boolean succes = dataBaseHelper.AddOneUser(userModel);

                    // on success or fail make toast to notify user
                    if(succes){
                        Toast.makeText(AccountCreation.this, "ACCOUNT CREATED", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(AccountCreation.this, "ACCOUNT CREATION FAILED", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    // Fields are empty notfiy user
                    Toast.makeText(AccountCreation.this, "FIELDS ARE EMPTY", Toast.LENGTH_SHORT).show();
                }


            }
        });


        // Go back to Login Screen
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // going back to mainactivity
                Intent intentMainActivity = new Intent(AccountCreation.this, LoginScreen.class);
                startActivity(intentMainActivity);
            }
        });


    }
}
