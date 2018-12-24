package com.midastouch.noraai.presenter;

import android.content.Context;
import android.graphics.Bitmap;

public interface IBarCodePresenter {
    void processImage(Bitmap inputBitmap, Context mContext);
}
