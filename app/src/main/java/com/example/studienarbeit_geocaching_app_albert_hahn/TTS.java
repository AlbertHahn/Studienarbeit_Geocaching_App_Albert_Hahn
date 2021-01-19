package com.example.studienarbeit_geocaching_app_albert_hahn;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * TextToSpeech class that extends services
 * will be called on request in the GeofenceBroadcastReceiver class
 */

public class TTS extends Service implements TextToSpeech.OnInitListener {

    /**
     * @value mTts object to start speech
     * @value spokenText that will obtained through intent passing
     */
    private TextToSpeech mTts;
    private String spokenText;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Initializes TTS and gains information for spoken text through extra intent
     */


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTts = new TextToSpeech(this, this);
        spokenText = intent.getStringExtra("MESSAGE");
        spokenText = spokenText + "was found!";
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * On call service will be initialized
     * @param status to determine success
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.ENGLISH);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    /**
     * Destroy service if done
     */

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            stopSelf();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}