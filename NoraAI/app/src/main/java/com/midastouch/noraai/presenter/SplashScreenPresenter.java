package com.midastouch.noraai.presenter;

import com.midastouch.noraai.view.ISplashScreenView;

public class SplashScreenPresenter implements ISplashScreenPresenter {

    ISplashScreenView splashScreenView;

    public SplashScreenPresenter(ISplashScreenView splashScreenView){
        this.splashScreenView = splashScreenView;
    }

}
