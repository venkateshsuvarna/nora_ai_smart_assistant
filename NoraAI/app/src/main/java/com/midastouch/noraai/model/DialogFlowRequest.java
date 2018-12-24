package com.midastouch.noraai.model;

import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.midastouch.noraai.presenter.DialogFlowResponseLogic;
import com.midastouch.noraai.view.IPrimaryActivityView;

import java.util.HashMap;

import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;
import ai.api.model.Status;

public class DialogFlowRequest {

    String requestString;
    AIConfiguration config;
    AIDataService aiDataService;
    AIRequest aiRequest;

    Status status;
    boolean requestCompleted;

    IPrimaryActivityView primaryActivityView;
    DialogFlowResponseLogic dialogFlowResponseLogic;

    Result result;
    String action;
    String resolvedQuery;
    String speech;
    HashMap<String,JsonElement> params;

    public DialogFlowRequest(String requestString, IPrimaryActivityView primaryActivityView){
        this.primaryActivityView = primaryActivityView;
        this.requestString = requestString;
        this.config = new AIConfiguration("INSERT YOUR DIALOGFLOW API KEY HERE",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiDataService = new AIDataService(config);
        aiRequest = new AIRequest();
        aiRequest.setQuery(requestString);

        new AIRequestVoidAIResponseAsyncTask().execute(aiRequest);

    }

    private class AIRequestVoidAIResponseAsyncTask extends AsyncTask<AIRequest, Void, AIResponse> {
        @Override
        protected AIResponse doInBackground(AIRequest... requests) {
            final AIRequest request = requests[0];
            try {
                final AIResponse response = aiDataService.request(aiRequest);
                return response;
            } catch (AIServiceException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(AIResponse aiResponse) {
            if (aiResponse != null) {
                // process aiResponse here
                status = aiResponse.getStatus();
                result = aiResponse.getResult();
                action = result.getAction();
                resolvedQuery = result.getResolvedQuery();
                speech = result.getFulfillment().getSpeech();
                params = result.getParameters();
                requestCompleted = true;

                DialogFlowResponseLogic dialogFlowResponseLogic = new DialogFlowResponseLogic(primaryActivityView,status,result,action,
                        resolvedQuery,speech,params,requestCompleted);

            }
        }
    }
}
