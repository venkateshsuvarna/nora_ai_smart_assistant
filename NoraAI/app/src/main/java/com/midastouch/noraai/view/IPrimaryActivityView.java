package com.midastouch.noraai.view;

public interface IPrimaryActivityView {
    void initializePrimaryActivity();
    void checkAudioRunTimePermission();
    void checkLocationRunTimePermission();
    void displayVoiceResultString(String resultString);
    void displayTextResultString(String resultString);
    void scrollMessageViewToBottom();
    void speakText(String textToSpeak);
    void noraDisplayMessage(String messageString);
    void noraDisplayResponseMessage(String messageString);
    void openAppAction(String messageString,String appName);
    void flipCoinAction(String messageString);
    void rollDiceAction(String messageString);
    void setTimerAction(String messageString,int timeInSeconds);
    void currentWeatherByCityAction(String messageString,String cityName,String weatherDescription,
                                    String weatherTemperature,String weatherHumidity,String weatherWindSpeed);
    void turnOnBluetooth(String messageString);
    void turnOffBluetooth(String messageString);
    void turnOnWifi(String messageString);
    void turnOffWifi(String messageString);
    void lyftToDestinationAction(String messageString, String lyftDestinationAddress);
    void sendTextOnWhatsAppAction(String messageString,String whatsAppTextString);
    void openScanCodeActivity();
    void showTutorialDemo();
    void playASongAction(String messageString);
}
