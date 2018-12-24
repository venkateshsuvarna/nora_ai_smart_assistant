package com.midastouch.noraai.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.alirezaashrafi.library.GoogleMapView;
import com.alirezaashrafi.library.MapScale;
import com.alirezaashrafi.library.MapType;
import com.keenfin.audioview.AudioView;
import com.lyft.lyftbutton.LyftButton;
import com.lyft.lyftbutton.LyftStyle;
import com.lyft.lyftbutton.RideParams;
import com.lyft.lyftbutton.RideTypeEnum;
import com.lyft.networking.ApiConfig;
import com.midastouch.noraai.MainActivity;
import com.midastouch.noraai.R;
import com.midastouch.noraai.model.DialogFlowRequest;
import com.midastouch.noraai.presenter.IPrimaryActivityPresenter;
import com.midastouch.noraai.presenter.PrimaryActivityPresenter;
import com.midastouch.noraai.presenter.SingleShotLocationProvider;
import com.wooplr.spotlight.SpotlightView;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import design.ivisionblog.apps.reviewdialoglibrary.FeedBackActionsListeners;
import design.ivisionblog.apps.reviewdialoglibrary.FeedBackDialog;
import me.anwarshahriar.calligrapher.Calligrapher;

public class PrimaryActivity extends AppCompatActivity implements IPrimaryActivityView {

    IPrimaryActivityPresenter primaryActivityPresenter;
    Toolbar primaryActivityToolbar;
    Context mContext = this;
    ImageButton micButton;
    boolean audioPermissionGranted;
    boolean locationPermissionGranted;
    LinearLayout linearLayoutMessageView;
    TextToSpeech textToSpeech;
    String welcomeText = "Hello I am Nora! How can I help you today?";
    ImageButton sendButton;
    EditText inputEditText;
    IPrimaryActivityView primaryActivityView = this;
    float currentLocationLatitude = 0.0f;
    float currentLocationLongitude = 0.0f;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary);

        //Binding the primary activity view to the presenter
        primaryActivityPresenter = new PrimaryActivityPresenter(this);

        //Initialize the UI Elements
        primaryActivityToolbar = findViewById(R.id.primaryActivityToolbar);
        micButton = findViewById(R.id.micButton);
        sendButton = findViewById(R.id.sendButton);
        inputEditText = findViewById(R.id.inputEditText);

        initializePrimaryActivity();

        //Function to ask user for runtime permissions
        checkAudioRunTimePermission();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scanCode:
                openScanCodeActivity();
                break;
            case R.id.showTutorial:
                showTutorialDemo();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        FeedBackDialog mDialog = new FeedBackDialog(mContext)
                .setBackgroundColor(R.color.colorPrimary)
                .setIcon(R.drawable.ic_launcher_round_small)
                .setIconColor(R.color.colorPrimaryDark)
                .setTitle(R.string.feedbackTitle)
                .setDescription(R.string.feedbackSubTitle)
                .setReviewQuestion(R.string.feedbackReviewQuestion)
                .setPositiveFeedbackText(R.string.feedbackPositiveReviewText)
                .setNegativeFeedbackText(R.string.feedbackNegativeReviewText)
                .setAmbiguityFeedbackText(R.string.feedbackAmbiguityReviewText)
                .setPositiveFeedbackIcon(R.mipmap.ic_feedback_positive)
                .setNegativeFeedbackIcon(R.mipmap.ic_feedback_negative)
                .setAmbiguityFeedbackIcon(R.mipmap.ic_feedback_average)
                .setOnReviewClickListener(new FeedBackActionsListeners() {
                    @Override
                    public void onPositiveFeedback(FeedBackDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onNegativeFeedback(FeedBackDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onAmbiguityFeedback(FeedBackDialog dialog) {
                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelListener(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                })
                .show();  // Finally don't forget to call show()

    }

    @SuppressLint("ClickableViewAccessibility") //This is because of onTouch accessibility Android Studio warning
    @Override
    public void initializePrimaryActivity() {

        //Initialize the toolbar
        if(primaryActivityToolbar != null){
            setSupportActionBar(primaryActivityToolbar);
            primaryActivityToolbar.setTitle("Nora.AI");
            primaryActivityToolbar.setSubtitle("online");
        }

        //Initialize the font for the activity
        //Calligrapher calligrapher = new Calligrapher(this);
        //calligrapher.setFont(this, "fonts/Montserrat-Medium.ttf", true);

        //Add the initial date to the message list
        View dateView = getLayoutInflater().inflate(R.layout.date_layout,null);
        TextView dateTextView = dateView.findViewById(R.id.dateTextView);
        linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        String pattern = "dd MMMM yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        dateTextView.setText(date);
        linearLayoutMessageView.addView(dateView);


        //Initialize the TTS Engine
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
                    textToSpeech.setSpeechRate(1.0f);
                    textToSpeech.setLanguage(Locale.UK);
                    textToSpeech.playSilentUtterance(3000,TextToSpeech.QUEUE_ADD,null);
                    speakText(welcomeText);

                } else {
                    Log.d("TTS","TTS Initialization failed!");
                }
            }
        },"com.google.android.tts");


        //Add the first message to the list
        View firstView = getLayoutInflater().inflate(R.layout.nora_message_layout, null);
        TextView firstMessageTextView = firstView.findViewById(R.id.noraMessageTextView);
        firstMessageTextView.setText(welcomeText);
        linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(firstView);


        //Initialize speech recognition
        primaryActivityPresenter.initializeSpeechRecognition(mContext);

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP:
                        primaryActivityToolbar.setSubtitleTextColor(Color.parseColor("#3B5992"));
                        primaryActivityToolbar.setSubtitle("online");
                        primaryActivityPresenter.stopSpeechRecognition();
                        break;

                    case MotionEvent.ACTION_DOWN:
                        checkAudioRunTimePermission();
                        primaryActivityToolbar.setSubtitleTextColor(Color.GREEN);
                        primaryActivityToolbar.setSubtitle("listening...");
                        primaryActivityPresenter.startSpeechRecognition();
                        break;
                }
                return false;
            }
        });

        micButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        //Set the onclick listener of the send button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = inputEditText.getText().toString();
                if(!inputText.equals("")){
                    DialogFlowRequest dialogFlowRequest = new DialogFlowRequest(inputText,primaryActivityView);
                }
                displayTextResultString(inputText);
                inputEditText.setText("");
            }
        });

    }

    @Override
    public void checkAudioRunTimePermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED){
            audioPermissionGranted = true;
        }
        else{
            audioPermissionGranted = false;
        }

        if(audioPermissionGranted == false){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    101);
        }

    }

    @Override
    public void checkLocationRunTimePermission() {

        if(ContextCompat.checkSelfPermission(PrimaryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationPermissionGranted = true;
        }
        else{
            locationPermissionGranted = false;
        }

        if(locationPermissionGranted == false){
            ActivityCompat.requestPermissions(PrimaryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

    }

    @Override
    public void displayVoiceResultString(String resultString) {

        //This function will display the result from the speech processing and add the result string
        //as a view in the message list

        View userMessageView = getLayoutInflater().inflate(R.layout.user_message_layout, null);
        TextView userMessageTextView = userMessageView.findViewById(R.id.userMessageTextView);
        userMessageTextView.setText(resultString);
        LinearLayout linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(userMessageView);
        scrollMessageViewToBottom();
    }

    @Override
    public void displayTextResultString(String resultString) {

        //This function will take the input from the input edittext and will be called inside the onclick
        //function of the send button - and will add the resultString to the message list

        if(!resultString.equals("")){
            View userMessageView = getLayoutInflater().inflate(R.layout.user_message_layout,null);
            TextView userMessageTextView = userMessageView.findViewById(R.id.userMessageTextView);
            userMessageTextView.setText(resultString);
            LinearLayout linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
            linearLayoutMessageView.addView(userMessageView);
            scrollMessageViewToBottom();
        }
        else{
            String writeSomethingString = "Try to write something in the text field or you can even speak into the microphone by long pressing the mic icon. Will try my best to answer your questions.";
            noraDisplayMessage(writeSomethingString);
            speakText(writeSomethingString);
        }

    }

    @Override
    public void scrollMessageViewToBottom() {

        //This function scrolls the content inside the messageview to the bottom - this is called every time
        //a new view is added to the message view

        final ScrollView messagesScrollView = ((ScrollView) findViewById(R.id.messagesScrollView));
        messagesScrollView.post(new Runnable() {
            @Override
            public void run() {
                messagesScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void speakText(String textToSpeak) {
        //This method will speak the response from the Dialogflow Speech Fullfilment to the user
        textToSpeech.speak(textToSpeak,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void noraDisplayMessage(String messageString) {
        View userMessageView = getLayoutInflater().inflate(R.layout.nora_message_layout,null);
        TextView userMessageTextView = userMessageView.findViewById(R.id.noraMessageTextView);
        userMessageTextView.setText(messageString);
        LinearLayout linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(userMessageView);
        scrollMessageViewToBottom();
    }

    @Override
    public void noraDisplayResponseMessage(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();
    }

    @Override
    public void openAppAction(String messageString, String appName) {

        //Toast.makeText(getApplicationContext(),"Open App action called",Toast.LENGTH_SHORT).show();

        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        Toast.makeText(getApplicationContext(),"App Name "+appName,Toast.LENGTH_SHORT).show();

        if(appName.equalsIgnoreCase("facebook")){
            String uri = "facebook://facebook.com/inbox";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
        else if(appName.equalsIgnoreCase("uber")){
            String uri = "uber://";
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }

    }

    @Override
    public void flipCoinAction(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();
    }

    @Override
    public void rollDiceAction(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();
    }

    @Override
    public void currentWeatherByCityAction(String messageString, String cityName, String weatherDescription, String weatherTemperature, String weatherHumidity, String weatherWindSpeed) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        View currentWeatherView = getLayoutInflater().inflate(R.layout.current_weather_layout,null);
        TextView cityNameTextView = currentWeatherView.findViewById(R.id.cityNameTextView);
        TextView currentTemperatureTextView = currentWeatherView.findViewById(R.id.currentTemperatureTextView);
        TextView currentHumidityTextView = currentWeatherView.findViewById(R.id.currentHumidityTextView);
        TextView currentWindSpeedTextView = currentWeatherView.findViewById(R.id.currentWindSpeedTextView);

        cityNameTextView.setText(cityName);
        currentTemperatureTextView.setText(weatherTemperature + "°F");
        currentHumidityTextView.setText(weatherHumidity + " %");
        currentWindSpeedTextView.setText(weatherWindSpeed + " mph");

        linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(currentWeatherView);

    }

    @Override
    public void lyftToDestinationAction(String messageString, String destinationAddress) {

        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        if(locationPermissionGranted == true){
            //Get the current location and set the latitude and longitude

            SingleShotLocationProvider.requestSingleUpdate(mContext,
                    new SingleShotLocationProvider.LocationCallback() {
                        @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                            Log.d("LyftLocation", "my location is "
                                    + location.latitude + " " + location.longitude);
                            currentLocationLatitude = location.latitude;
                            currentLocationLongitude = location.longitude;
                        }
                    });
        }
        else{
            checkLocationRunTimePermission();
        }

        //Initialize the Google Map View
        View googleMapCurrentLocation = getLayoutInflater().inflate(R.layout.google_maps_current_location,null);
        GoogleMapView googleMapView = googleMapCurrentLocation.findViewById(R.id.googleMapView);
        googleMapView.setLatitude(currentLocationLatitude);
        googleMapView.setLongitude(currentLocationLongitude);
        googleMapView.setMapScale(MapScale.HIGH);
        googleMapView.setMapZoom(15);
        googleMapView.setMapType(MapType.ROADMAP);
        googleMapView.setZoomable(PrimaryActivity.this);
        googleMapView.setMapHeight(300);

        Toast.makeText(getApplicationContext(),"Lat = "+currentLocationLatitude +
        "Lon" + currentLocationLongitude,Toast.LENGTH_LONG).show();

        linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(googleMapCurrentLocation);
        scrollMessageViewToBottom();

        View lyftButtonLayout = getLayoutInflater().inflate(R.layout.lyft_button_layout,null);
        LyftButton lyftButton = lyftButtonLayout.findViewById(R.id.lyftButton);

        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("gtPa1F6kOnHa")
                .setClientToken("SkjeR0HsYiaP1taD0dELVHd76RUd7+JXF/0DsIjwYUn4NSKyf6Kv8RQhghuc3oceJNSw5sxehALmb/vykv5bMKwwgZCb6vWUxVTR5Tcn2qpa8/5ZAN59k8g=")
                .build();

        lyftButton.setApiConfig(apiConfig);
        RideParams.Builder rideParamsBuilder = new RideParams.Builder()
              .setPickupLocation(currentLocationLatitude,currentLocationLongitude)
               .setDropoffAddress(destinationAddress);
        //RideParams.Builder rideParamsBuilder = new RideParams.Builder()
        //        .setPickupLocation(37.7766048, -122.3943576)
        //        .setDropoffLocation(37.759234, -122.4135125);
        rideParamsBuilder.setRideTypeEnum(RideTypeEnum.CLASSIC);
        lyftButton.setRideParams(rideParamsBuilder.build());
        lyftButton.setLyftStyle(LyftStyle.HOT_PINK);
        lyftButton.load();

        linearLayoutMessageView.addView(lyftButtonLayout);
        scrollMessageViewToBottom();

    }

    @Override
    public void sendTextOnWhatsAppAction(String messageString, String whatsAppTextString) {
        speakText(messageString);
        noraDisplayMessage(messageString);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.putExtra(Intent.EXTRA_TEXT, whatsAppTextString);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);

    }

    @Override
    public void setTimerAction(String messageString, int timeInSeconds) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        View timerView = getLayoutInflater().inflate(R.layout.set_timer_layout,null);
        final TextView timerLiveTextView = timerView.findViewById(R.id.timerLiveTextView);
        Button stopTimerButton = timerView.findViewById(R.id.stopTimerButton);

        LinearLayout linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(timerView);

        Toast.makeText(getApplicationContext(),"Time in seconds "+timeInSeconds,Toast.LENGTH_SHORT).show();

        final CountDownTimer cdt = new CountDownTimer(timeInSeconds * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                timerLiveTextView.setText(millisUntilFinished/1000 + " seconds");
            }

            public void onFinish() {
                timerLiveTextView.setText("Done");
                timerLiveTextView.setTextColor(Color.RED);
            }

        }.start();

        stopTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerLiveTextView.setText("Done");
                timerLiveTextView.setTextColor(Color.RED);
                cdt.cancel();
            }
        });

        scrollMessageViewToBottom();

    }

    @Override
    public void turnOnBluetooth(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.enable();
        }
    }

    @Override
    public void turnOffBluetooth(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }

    }

    @Override
    public void turnOnWifi(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        if(isWifiEnabled == false){
            wifiManager.setWifiEnabled(true);
        }
    }

    @Override
    public void turnOffWifi(String messageString) {
        speakText(messageString);
        noraDisplayMessage(messageString);
        scrollMessageViewToBottom();

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean isWifiEnabled = wifiManager.isWifiEnabled();
        if(isWifiEnabled == true){
            wifiManager.setWifiEnabled(false);
        }

    }

    @Override
    public void openScanCodeActivity() {
        Intent intent = new Intent(PrimaryActivity.this,BarCodeActivity.class);
        startActivity(intent);
    }

    @Override
    public void showTutorialDemo() {
        new SpotlightView.Builder(this)
                .introAnimationDuration(500)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(Color.parseColor("#eb273f"))
                .headingTvSize(32)
                .headingTvText("Love")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Like the picture?\nLet others know.")
                .maskColor(Color.parseColor("#dc000000"))
                .target(findViewById(R.id.inputEditText))
                .lineAnimDuration(400)
                .lineAndArcColor(Color.parseColor("#eb273f"))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId("105") //UNIQUE ID
                .show();
    }

    @Override
    public void playASongAction(String messageString) {
        View audioPlayerLayout = getLayoutInflater().inflate(R.layout.audio_player_layout,null);
        AudioView audioView = audioPlayerLayout.findViewById(R.id.audioView);
        speakText(messageString);
        noraDisplayMessage(messageString);

        linearLayoutMessageView = findViewById(R.id.linearLayoutMessageView);
        linearLayoutMessageView.addView(audioPlayerLayout);

        try{
            audioView.setDataSource(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.bensound_testsong));
            audioView.start();
        }
        catch (IOException e){
            //Do nothing
            Toast.makeText(getApplicationContext(),"Audio File Not Found",Toast.LENGTH_LONG).show();
        }

        scrollMessageViewToBottom();

    }
}
