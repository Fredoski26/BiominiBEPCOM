package com.biomini.sample;

import static android.service.controls.ControlsProviderService.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.bepcom.constant.Constant;
import com.example.bepcom.model.LoginModel;
import com.example.bepcom.network.Api;
import com.example.bepcom.network.ApiInterface;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity {

    AppCompatButton login;
    AppCompatButton exit;
    TextView fileName;
    TextView pass;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(AuthActivity.this, R.color.colorPrimary));
        }
        inits();
    }
    private void inits() {
        login = findViewById(R.id.login);
        exit = findViewById(R.id.exit);
        fileName = findViewById(R.id.fileNumber);
        pass = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        login.setOnClickListener(v -> {
            if (fileName.getText().toString().trim().isEmpty()) {
                fileName.setError("enter file number");
                Toast.makeText(getBaseContext(), "Enter file number", Toast.LENGTH_LONG).show();

            } else if (pass.getText().toString().trim().isEmpty()) {
                pass.setError("enter password");
                Toast.makeText(getBaseContext(), "Enter password", Toast.LENGTH_LONG).show();

            } else {
                apiLogin();
            }

        });
        exit.setOnClickListener(v -> onBackPressed());

    }


    private void apiLogin() {
        login.setVisibility(View.GONE);
        final String fileNumber2 = fileName.getText().toString();
        final String pass2 = pass.getText().toString();

        JsonObject object = new JsonObject();
        object.addProperty("file_number", fileNumber2);
        object.addProperty("password", pass2);
        // Toast.makeText(this, "Your Number " + fileNumber2, Toast.LENGTH_SHORT).show();

        ApiInterface api = Api.CreateNodeApi();
        progressBar.setVisibility(View.VISIBLE);
        final Call<LoginModel> loginModelCall = api.getLogin(object);
        loginModelCall.enqueue(new Callback<LoginModel>() {

            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                Log.d(TAG, "onResponse: " + response);
                if (response.isSuccessful() && response.errorBody() == null) {
                    LoginModel loginModel = response.body();
                    assert response.body() != null;
                    try {
                        if ((response.body().getStatus_code() == 200)) {
                            Log.d(TAG, "token" + response.body().getToken());

                            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
                            intent.putExtra("fullName", loginModel.getPersonal_details().getName());
                            //intent.putExtra("token", response.body().getToken());
                            Constant.token = response.body().getToken();
                            Constant.names = loginModel.getPersonal_details().getName();
                            Constant.fileNumber = loginModel.getPersonal_details().getFile_number();
                            startActivity(intent);
                            finish();

                        } else {
                            login.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);

                            Toast.makeText(AuthActivity.this, "Your login failed! Please verify your credentials and try again", Toast.LENGTH_SHORT).show();

                        }

                    }catch (Exception e){
                        Log.d("loginModelCall",e.getMessage());

                    }
                }

                if (response.code() == 422) {
                    login.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AuthActivity.this, "Something went wrong! Check and try again", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                login.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AuthActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                Log.d("failed ", t.toString());
            }
        });
    }
}