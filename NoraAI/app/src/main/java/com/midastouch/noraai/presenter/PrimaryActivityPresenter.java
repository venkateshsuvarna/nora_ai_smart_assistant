package com.midastouch.noraai.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;

import com.midastouch.noraai.model.DialogFlowRequest;
import com.midastouch.noraai.view.IPrimaryActivityView;

import java.util.ArrayList;
import java.util.Locale;

public class PrimaryActivityPresenter implements IPrimaryActivityPresenter {

    IPrimaryActivityView primaryActivityView;

    SpeechRecognizer mSpeechRecognizer;
    Intent mSpeechRecognizerIntent;

    public PrimaryActivityPresenter(IPrimaryActivityView primaryActivityView){
        this.primaryActivityView = primaryActivityView;
    }

    @Override
    public void initializeSpeechRecognition(Context mContext) {
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        mSpeechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matches!=null){
                    primaryActivityView.displayVoiceResultString(matches.get(0));
                    DialogFlowRequest dialogFlowRequest = new DialogFlowRequest(matches.get(0),primaryActivityView);
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    @Override
    public void startSpeechRecognition() {
        mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
    }

    @Override
    public void stopSpeechRecognition() {
        mSpeechRecognizer.stopListening();
    }
}
