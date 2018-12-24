package com.midastouch.noraai.presenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.kwabenaberko.openweathermaplib.Lang;
import com.kwabenaberko.openweathermaplib.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.models.currentweather.CurrentWeather;
import com.midastouch.noraai.view.IPrimaryActivityView;

import java.util.HashMap;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.Result;
import ai.api.model.Status;

public class DialogFlowResponseLogic {

    IPrimaryActivityView primaryActivityView;

    String requestString;
    AIConfiguration config;
    AIDataService aiDataService;
    AIRequest aiRequest;
    Status status;
    boolean requestCompleted;

    Result result;
    String action;
    String resolvedQuery;
    String speech;
    HashMap<String,JsonElement> params;

    String currentCityName;
    String lyftDestinationAddress;
    String whatsAppTextString;

    public DialogFlowResponseLogic(final IPrimaryActivityView primaryActivityView, Status status, Result result,
                                   String action, String resolvedQuery, final String speech,
                                   HashMap<String, JsonElement> params, boolean requestCompleted){

        Log.d("TestCommand","DialogFlowResponse Logic Constructor called");

        this.primaryActivityView = primaryActivityView;
        this.status = status;
        this.result = result;
        this.action = action;
        this.resolvedQuery = resolvedQuery;
        this.speech = speech;
        this.params = params;
        this.requestCompleted = requestCompleted;

        Log.d("TestCommand","Speech "+speech);

        //this.primaryActivityView.noraDisplayResponseMessage(speech);

        if(action.equals("open_app_action")){
            String appName = "";
            for (final Map.Entry<String, JsonElement> entry : params.entrySet()) {
                if(entry.getKey().equals("app_name")) {
                    appName = entry.getValue().toString();
                    appName = appName.substring(1,appName.length()-1);
                }
            }
            this.primaryActivityView.openAppAction(speech,appName);
        }
        else if(action.equals("flip_coin_action")){
            this.primaryActivityView.flipCoinAction(speech);
        }
        else if(action.equals("roll_dice_action")){
            this.primaryActivityView.rollDiceAction(speech);
        }
        else if(action.equals("set_timer_action")){
            int timeInSeconds = 10;
            for(final Map.Entry<String,JsonElement> entry : params.entrySet()){
                if(entry.getKey().equalsIgnoreCase("time_in_seconds")){
                    String timeInSecondsString = entry.getValue().toString();
                    timeInSecondsString = timeInSecondsString.substring(1,timeInSecondsString.length()-1);
                    timeInSeconds = Integer.valueOf(timeInSecondsString);
                }
            }
            this.primaryActivityView.setTimerAction(speech,timeInSeconds);
        }
        else if(action.equals("weather_by_city_action")){

            Log.d("WeatherAction","Weather Action detected");

            for(final Map.Entry<String,JsonElement> entry : params.entrySet()){
                if(entry.getKey().equalsIgnoreCase("weather_city")){
                    currentCityName = entry.getValue().toString();
                    currentCityName = currentCityName.substring(1,currentCityName.length()-1);
                    Log.d("WeatherAction","Current City "+currentCityName);
                }
            }
            OpenWeatherMapHelper helper = new OpenWeatherMapHelper();
            helper.setApiKey(ApiKey.OPEN_WEATHER_MAP_API_KEY);
            helper.setUnits(Units.IMPERIAL);
            helper.setLang(Lang.ENGLISH);
            helper.getCurrentWeatherByCityName(currentCityName, new OpenWeatherMapHelper.CurrentWeatherCallback() {
                @Override
                public void onSuccess(CurrentWeather currentWeather) {
                    Log.d("WeatherAction","Success");
                    Log.v("WeatherDataCity",
                            "Coordinates: " + currentWeather.getCoord().getLat() + ", "+currentWeather.getCoord().getLon()+"\n"
                                    +"Weather Description: " + currentWeather.getWeatherArray().get(0).getDescription() + "\n"
                                    +"Max Temperature: " + currentWeather.getMain().getTempMax()+"\n"
                                    +"Wind Speed: " + currentWeather.getWind().getSpeed() + "\n"
                                    +"City, Country: " + currentWeather.getName() + ", " + currentWeather.getSys().getCountry()
                    );

                    String weatherDescription = currentWeather.getWeatherArray().get(0).getDescription();
                    String weatherTemperature = String.valueOf(currentWeather.getMain().getTemp());
                    String weatherHumidity = String.valueOf(currentWeather.getMain().getHumidity());
                    String weatherWindSpeed = String.valueOf(currentWeather.getWind().getSpeed());

                    primaryActivityView.currentWeatherByCityAction(speech,currentCityName,weatherDescription,
                            weatherTemperature,weatherHumidity,weatherWindSpeed);

                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d("WeatherAction","Failure");
                    Log.d("WeatherAction","Throwable Message = "+throwable.getMessage());
                    //Log.v(TAG, throwable.getMessage());
                }
            });

        }
        else if(action.equals("turn_on_bluetooth_action")){
            this.primaryActivityView.turnOnBluetooth(speech);
        }
        else if(action.equals("turn_off_bluetooth_action")){
            this.primaryActivityView.turnOffBluetooth(speech);
        }
        else if(action.equals("turn_on_wifi_action")){
            this.primaryActivityView.turnOnWifi(speech);
        }
        else if(action.equals("turn_off_wifi_action")){
            this.primaryActivityView.turnOffWifi(speech);
        }
        else if(action.equals("send_text_on_whatsapp_action")){

            for(final Map.Entry<String,JsonElement> entry : params.entrySet()){
                if(entry.getKey().equalsIgnoreCase("whatsapp_text")){
                    whatsAppTextString = entry.getValue().toString();
                    whatsAppTextString = whatsAppTextString.substring(1,whatsAppTextString.length()-1);
                }
            }
            this.primaryActivityView.sendTextOnWhatsAppAction(speech,whatsAppTextString);
        }
        else if(action.equals("lyft_to_destination_action")){
            for(final Map.Entry<String,JsonElement> entry : params.entrySet()){
                if(entry.getKey().equalsIgnoreCase("destination_address")){
                    lyftDestinationAddress = entry.getValue().toString();
                    lyftDestinationAddress = lyftDestinationAddress.substring(1,lyftDestinationAddress.length()-1);
                }
            }
            this.primaryActivityView.lyftToDestinationAction(speech,lyftDestinationAddress);
        }
        else if(action.equals("play_a_song_action")){
            this.primaryActivityView.playASongAction(speech);
        }
        else{
            this.primaryActivityView.noraDisplayResponseMessage(speech);
        }


    }


    public String getRequestString() {
        return requestString;
    }

    public AIConfiguration getConfig() {
        return config;
    }

    public AIDataService getAiDataService() {
        return aiDataService;
    }

    public AIRequest getAiRequest() {
        return aiRequest;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isRequestCompleted() {
        return requestCompleted;
    }

    public Result getResult() {
        return result;
    }

    public String getAction() {
        return action;
    }

    public String getResolvedQuery() {
        return resolvedQuery;
    }

    public String getSpeech() {
        return speech;
    }

    public HashMap<String, JsonElement> getParams() {
        return params;
    }
}
