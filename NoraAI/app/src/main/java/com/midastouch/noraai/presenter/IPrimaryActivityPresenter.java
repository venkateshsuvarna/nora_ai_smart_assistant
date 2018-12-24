package com.midastouch.noraai.presenter;

import android.content.Context;

public interface IPrimaryActivityPresenter {
    void initializeSpeechRecognition(Context mContext);
    void startSpeechRecognition();
    void stopSpeechRecognition();
}
