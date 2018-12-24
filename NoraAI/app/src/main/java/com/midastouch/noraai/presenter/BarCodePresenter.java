package com.midastouch.noraai.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.midastouch.noraai.view.IBarCodeActivityView;

import java.util.List;

public class BarCodePresenter implements IBarCodePresenter {
    IBarCodeActivityView barCodeActivityView;

    public BarCodePresenter(IBarCodeActivityView barCodeActivityView){
        this.barCodeActivityView = barCodeActivityView;
    }

    @Override
    public void processImage(Bitmap inputBitmap, Context mContext) {
        Log.d("BarcodeTest","Process Image Called");
        FirebaseApp.initializeApp(mContext);
        FirebaseVisionBarcodeDetectorOptions options = new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_ALL_FORMATS)
                        .build();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(inputBitmap);
        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                        Log.d("BarcodeTest","Success Block Reached");
                        // Task completed successfully
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Log.d("BarcodeTest","One barcode detected with :");
                            Log.d("BarcodeTest","Display Value :"+barcode.getDisplayValue());
                            if(barcode.getValueType() == FirebaseVisionBarcode.TYPE_ISBN){
                                Log.d("BarcodeTest","ISBN type detected");
                            }
                            if(barcode.getValueType() == FirebaseVisionBarcode.FORMAT_QR_CODE){
                                Log.d("BarcodeTest","QR Code detected");
                            }
                            Log.d("BarcodeTest","Barcode Type : "+barcode.getValueType());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        Log.d("BarcodeRawValue","Failure Block Reached");
                    }
                });

    }
}
