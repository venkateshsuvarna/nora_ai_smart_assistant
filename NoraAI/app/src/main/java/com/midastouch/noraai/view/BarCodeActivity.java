package com.midastouch.noraai.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.midastouch.noraai.R;
import com.midastouch.noraai.presenter.BarCodePresenter;
import com.midastouch.noraai.presenter.IBarCodePresenter;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Facing;
import com.otaliastudios.cameraview.Flash;
import com.otaliastudios.cameraview.Gesture;
import com.otaliastudios.cameraview.GestureAction;
import com.otaliastudios.cameraview.Grid;
import com.otaliastudios.cameraview.Hdr;
import com.otaliastudios.cameraview.SessionType;
import com.otaliastudios.cameraview.WhiteBalance;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BarCodeActivity extends AppCompatActivity implements IBarCodeActivityView{

    CameraView cameraView;
    final Context mContext = this;
    Button backArrowButton;
    Button scanCodeButton;
    IBarCodePresenter barCodePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_barcode_scanner);
        super.onCreate(savedInstanceState);

        //Initialize the presenters
        barCodePresenter = new BarCodePresenter(this);

        initializeBarCodeActivity();
    }

    @Override
    public void initializeBarCodeActivity() {

        //Initialize Firebase
        FirebaseApp.initializeApp(mContext);

        //Initialize the views by id

        cameraView = findViewById(R.id.cameraView);

        if(cameraView == null){
            Log.d("BarcodeTest","CameraKitView is null");
        }

        backArrowButton = findViewById(R.id.backArrowButton);
        scanCodeButton = findViewById(R.id.scanCodeButton);

        cameraView.setLifecycleOwner(this);
        cameraView.setSessionType(SessionType.PICTURE);
        cameraView.setFacing(Facing.BACK);
        cameraView.setFlash(Flash.AUTO);
        cameraView.setJpegQuality(100);
        cameraView.setWhiteBalance(WhiteBalance.AUTO);
        cameraView.setHdr(Hdr.ON);
        cameraView.setPlaySounds(true);

        cameraView.mapGesture(Gesture.PINCH, GestureAction.ZOOM); // Pinch to zoom!
        cameraView.mapGesture(Gesture.TAP, GestureAction.FOCUS_WITH_MARKER); // Tap to focus!
        cameraView.mapGesture(Gesture.LONG_TAP, GestureAction.CAPTURE); // Long tap to shoot!

        cameraView.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] jpeg) {
                Bitmap inputBitmap = BitmapFactory.decodeByteArray(jpeg,0,jpeg.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                inputBitmap = Bitmap.createBitmap(inputBitmap,0,0,inputBitmap.getWidth(),inputBitmap.getHeight(),matrix,true);
                barCodePresenter.processImage(inputBitmap,mContext);
            }
        });

        //Set the functionality of the back button
        backArrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //It should go back to the PrimaryActivity
            }
        });

        scanCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cameraView.capturePicture();
                Toast.makeText(getApplicationContext(),"Picture Captured",Toast.LENGTH_SHORT).show();
            }
        });



    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
