package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity class for the splash screen
 * that starts as first activity
 */

public class SplashScreenActivity extends AppCompatActivity {
    final int SPLASH_DISPLAY_LENGTH = 6000;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/

       mHandler = new Handler();
       mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreenActivity.this, LoginScreen.class);
                SplashScreenActivity.this.startActivity(mainIntent);
                SplashScreenActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent mainIntent = new Intent(SplashScreenActivity.this, LoginScreen.class);
        SplashScreenActivity.this.startActivity(mainIntent);
        SplashScreenActivity.this.finish();
        mHandler.removeCallbacks(mRunnable);

        return super.onTouchEvent(event);
    }

}
