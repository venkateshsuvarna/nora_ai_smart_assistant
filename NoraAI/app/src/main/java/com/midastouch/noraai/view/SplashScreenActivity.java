package com.midastouch.noraai.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.midastouch.noraai.R;
import com.midastouch.noraai.presenter.ISplashScreenPresenter;
import com.midastouch.noraai.presenter.SplashScreenPresenter;
import com.midastouch.noraai.presenter.TypeWriter;

import java.util.HashMap;
import java.util.Locale;

import me.anwarshahriar.calligrapher.Calligrapher;

public class SplashScreenActivity extends AppCompatActivity implements ISplashScreenView{

    ISplashScreenPresenter splashScreenPresenter;
    TextView noraAITextView;
    ImageView noraAIImageView;
    TextToSpeech textToSpeech;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        //Create the presenter object and pass this view to the interface
        splashScreenPresenter = new SplashScreenPresenter(this);

        //Initialize the UI Components
        noraAITextView = findViewById(R.id.noraAITextView);
        noraAIImageView = findViewById(R.id.noraAIImageView);

        //Call initialActivity which will initialize the activity - hide the action bar and set custom font
        initializeSplashScreenActivity();
    }

    @Override
    public void initializeSplashScreenActivity() {

        //Initialize the font for the activity
        //Calligrapher calligrapher = new Calligrapher(this);
        //calligrapher.setFont(this, "fonts/Montserrat-Medium.ttf", true);

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f,1.0f);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f,0.0f);
        fadeIn.setDuration(3500);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(3500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(5000+fadeIn.getStartOffset());
        noraAIImageView.startAnimation(fadeOut);
        noraAIImageView.startAnimation(fadeIn);

        //Make blinking animation of the Nora.AI text
        AlphaAnimation fadeIn1 = new AlphaAnimation(0.0f,1.0f);
        AlphaAnimation fadeOut1 = new AlphaAnimation(1.0f,0.0f);
        fadeIn1.setDuration(3500);
        fadeIn1.setFillAfter(true);
        fadeOut1.setDuration(3500);
        fadeOut1.setFillAfter(true);
        fadeOut1.setStartOffset(5000+fadeIn1.getStartOffset());
        noraAITextView.startAnimation(fadeOut1);
        noraAITextView.startAnimation(fadeIn1);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = textToSpeech.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.d("TTS", "The Language is not supported!");
                    } else {
                        Log.d("TTS", "Language Supported.");
                    }
                    Log.d("TTS", "Initialization success.");

                    textToSpeech.setPitch(1.15f);
                    textToSpeech.setSpeechRate(0.8f);
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.speak("Putting the smart in smartphones.", TextToSpeech.QUEUE_ADD, null);

                } else {
                    Log.d("TTS","TTS Initialization failed!");
                }
            }
        },"com.google.android.tts");

        //Start the animation of the typing textview

        TypeWriter typeWriterTextView = findViewById(R.id.typeWriterTextView);
        typeWriterTextView.setText("");
        typeWriterTextView.setCharacterDelay(100);
        typeWriterTextView.animateText("Putting the smart in smartphones.");


        //Start new activity with slide right animation after 2 seconds
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this,PrimaryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                if(textToSpeech!=null){
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                }
                finish();
            }
        },8000);

    }
}
