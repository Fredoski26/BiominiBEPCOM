package com.biomini.sample;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassportCaptureActivity extends AppCompatActivity {

    ImageView userImage;
    AppCompatButton done;
    AppCompatButton back;
    TextView retake;

    ProgressBar progressBar;
    public String token = "";

    public String base64Image;

    private MultipartBody gone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passport_capture);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(PassportCaptureActivity.this, R.color.colorPrimary));
        }
        inits();
    }
        private void inits() {
            userImage = findViewById(R.id.userImage);
            done = findViewById(R.id.done);
            back = findViewById(R.id.back);
            retake = findViewById(R.id.retakeCapture);
            progressBar = findViewById(R.id.progressBar);


            // token = getIntent().getStringExtra("token");
            base64Image = getIntent().getStringExtra("photo");

            Bitmap imageBitmap = decodeBase64(base64Image);
            userImage.setImageBitmap(imageBitmap);



            back.setOnClickListener(v -> {
                startActivity(new Intent(getBaseContext(), HomeActivity.class));
            });

            done.setOnClickListener(v -> apiUploadPassport());
            retake.setOnClickListener(v -> {
                startActivity(new Intent(getBaseContext(), HomeActivity.class));
                finish();
            });





        }


        private Bitmap decodeBase64(String base64Image) {
            byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        }

        private void apiUploadPassport() {
            String dataImage = "data:image/jpeg;base64,";
            String imageBase64 = dataImage + base64Image;
            done.setVisibility(View.GONE);

            com.example.bepcom.network.ApiInterface api = com.example.bepcom.network.Api.CreateNodeApi();
       /* PassportRequest passportRequest = new PassportRequest();
        passportRequest.setPassport(imageBase64);*/
            JsonObject object = new JsonObject();
            object.addProperty("passport",imageBase64);

            progressBar.setVisibility(View.VISIBLE);
            final Call<com.example.bepcom.model.PassportModel> passportModelCall = api.getPassport(object);
            Log.d(TAG, "onWithPassport:   " +object);
            passportModelCall.enqueue(new Callback<com.example.bepcom.model.PassportModel>() {
                @Override
                public void onResponse(@NonNull Call<com.example.bepcom.model.PassportModel> call, @NonNull Response<com.example.bepcom.model.PassportModel> response) {
                    Log.d(TAG, "onWithFred:   " +response);

                    if(response.isSuccessful() && response.errorBody() == null){
                        assert response.body() != null;
                        try {
                            if((response.body().getStatus_code() == 200)){

                                Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                                intent.putExtra("message", response.body().getMessage());
                                startActivity(intent);
                                finish();
                                Toast.makeText(PassportCaptureActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            }else {
                                done.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(PassportCaptureActivity.this, "Failed to Upload please try again", Toast.LENGTH_SHORT).show();

                            }

                        }catch (Exception e){
                            Log.d("passportModel",e.getMessage());
                        }
                    }

                    if (response.code() == 422) {
                        done.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PassportCaptureActivity.this, "Something went wrong! please try again", Toast.LENGTH_SHORT).show();

                    }
                }

                @Override
                public void onFailure(@NonNull Call<com.example.bepcom.model.PassportModel> call, @NonNull Throwable t) {
                    Toast.makeText(PassportCaptureActivity.this, "Something went wrong! please try again", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                    Log.d("Throwable  ------- > ", t.toString());
                    done.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                }
            });


        }
    }