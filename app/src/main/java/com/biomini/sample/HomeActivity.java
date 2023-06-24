package com.biomini.sample;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

public class HomeActivity extends AppCompatActivity {

    ImageView fingerScanner;
    ImageView capTure;
    TextView userName, addCaptureCompleted, addCapture;
    AppCompatButton logOut;

    public String edit;
    public String token = "";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_CAMERA = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(HomeActivity.this, R.color.colorPrimary));
        }
        inits();
    }


    private void inits() {
        String name = getIntent().getStringExtra("fullName");
        String message = getIntent().getStringExtra("message");



        addCapture = findViewById(R.id.addCapture);
        addCaptureCompleted = findViewById(R.id.addCaptureCompleted);
        userName = findViewById(R.id.userName);
        fingerScanner = findViewById(R.id.fingerScanner);
        capTure = findViewById(R.id.capture);
        logOut = findViewById(R.id.logOut);



        //Toast.makeText(this, ""+token, Toast.LENGTH_SHORT).show();
        userName.setText(com.example.bepcom.constant.Constant.names);


        logOut.setOnClickListener(v -> onBackPressed()

        );
        capTure.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                dispatchTakePictureIntent();
            } else {
                requestCameraPermission();
            }
        });

        fingerScanner.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), FingerPrintActivity.class));
            finish();
        });

        if (message == null){
            addCapture.setVisibility(View.VISIBLE);
            Log.d(TAG,"Retry");
        }else {
            addCapture.setVisibility(View.GONE);
            addCaptureCompleted.setVisibility(View.VISIBLE);
        }

    }


    private boolean checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_CAMERA);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                try {
                    if (imageBitmap != null) {
                        //capTure.setImageBitmap(imageBitmap);
                        String base64Image = encodeToBase64(imageBitmap);
                        Intent intent = new Intent(getBaseContext(), PassportCaptureActivity.class);
                        intent.putExtra("photo", base64Image);
                        //Log.d(TAG, "check  "+base64Image);
                        //intent.putExtra("token", token);
                        startActivity(intent);
                        finish();
                        this.setResult(RESULT_OK);
                        finish();
                        // Use the base64Image as per your requirement
                        //Toast.makeText(this, "Image captured and encoded as Base64.  "+imageBitmap, Toast.LENGTH_SHORT).show();
                    } else {
                        this.setResult(RESULT_CANCELED);
                        Toast.makeText(this, "cancel", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }catch (Exception e){
                    Log.d("imageBitmap",e.getMessage());
                }
            }
        }
    }

    private String encodeToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}