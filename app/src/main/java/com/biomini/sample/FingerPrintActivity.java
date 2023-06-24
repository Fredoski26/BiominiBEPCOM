package com.biomini.sample;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.suprema.BioMiniFactory;
import com.suprema.CaptureResponder;
import com.suprema.IBioMiniDevice;
import com.suprema.IUsbEventHandler;
import com.telpo.tps550.api.fingerprint.FingerPrint;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FingerPrintActivity extends AppCompatActivity {
    AppCompatButton exit, done;
    // private BioMiniFactory mBioMiniFactory = null;
    //private IBioMiniDevice mCurrentDevice = null;
    public static Bitmap bitmap;

    ProgressBar progressBar;
    ImageView right_thumb;
    ImageView Left_thumb;
    ImageView right_index;
    ImageView left_index;
    ImageView right_middle;
    //ImageView left_middle;

    Button right_thumb_text;
    Button left_thumb_text;
    Button right_index_text;
    Button left_index_text;


    Button Left_middle_text;
    Button Right_middle_text;
    private FingerPrintActivity mainContext;

    Map<String, byte[]> fingerprintImage = new HashMap<>();
    Map<String, String> fingerBase64 = new HashMap<>();


    public static final boolean mbUsbExternalUSBManager = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbManager mUsbManager = null;
    private PendingIntent mPermissionIntent = null;
    //


    private static BioMiniFactory mBioMiniFactory = null;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    public IBioMiniDevice mCurrentDevice = null;


    public final static String TAG = "BioMini Sample";
    private EditText mLogView;
    private ScrollView mScrollLog = null;

    private ViewPager mPager;

    /* private int []mNaviPicks= { R.id.pageindexImage_0 , R.id.pageindexImage_1 , R.id.pageindexImage_2, R.id.pageindexImage_3};

     int nInfComponents [] = {R.id.editLog, R.id.scrollLog ,
             R.id.seekBarSensitivity , R.id.seekBarSecurityLevel , R.id.seekBarTimeout , R.id.checkBoxFastMode , R.id.checkBoxCropMode, R.id.buttonReadCaptureParam , R.id.buttonWriteCaptureParam,
             R.id.buttonEnroll , R.id.buttonVerify , R.id.buttonDeleteAll ,
             R.id.buttonExportBmp, R.id.buttonExportWsq , R.id.buttonTemplate, R.id.button19794_4};

     int nLayouts[] = { R.layout.log_view , R.layout.setting_capture , R.layout.enrollment , R.layout.export};*/
    class UserData {
        String name;
        byte[] template;

        public UserData(String name, byte[] data, int len) {
            this.name = name;
            this.template = Arrays.copyOf(data, len);
        }
    }

    private ArrayList<UserData> mUsers = new ArrayList<>();

    private IBioMiniDevice.CaptureOption mCaptureOptionDefault = new IBioMiniDevice.CaptureOption();

    /*Single Capture*/
    private CaptureResponder mCaptureResponseDefault = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            Log.d(TAG, "onCapture : Capture successful!");
            //  printState(getResources().getText(R.string.capture_single_ok));
            // log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.Left_Thumb);
                        if (iv != null) {

                           // byte[] wsq = mCurrentDevice.getCaptureImageAsWsq(-1, -1, 3.5f, 0);
                           // fingerprintImage.put("Left_Thumb", wsq);

                            /*ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            capturedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            String Left_middle = Base64.encodeToString(byteArray, Base64.DEFAULT);*/



                           // String Left_middleBase64Image = encodeToBase64(capturedImage);
                            //fingerBase64.put("Left_Thumb", capturedImage.toString());
                           // Toast.makeText(mainContext, "Base64"+ Left_middleBase64Image.toString(), Toast.LENGTH_SHORT).show();
                            saveLeftFingers();
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            Log.d(TAG, "onCaptureError : " + error + " ErrorCode :" + errorCode);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                Log.d(TAG, "printState : " + error + " ErrorCode :" + error);
            //  printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };

    private CaptureResponder mCaptureResponseDefault2 = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            Log.d(TAG, "onCapture : Capture successful!");
            //  printState(getResources().getText(R.string.capture_single_ok));

            // log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.right_Thumb);
                        if (iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            Log.d(TAG, "onCaptureError : " + error + " ErrorCode :" + errorCode);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                Log.d(TAG, "printState : " + error + " ErrorCode :" + error);
            //  printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };

    private CaptureResponder mCaptureResponseDefault3 = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            Log.d(TAG, "onCapture : Capture successful!");
            //  printState(getResources().getText(R.string.capture_single_ok));

            // log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.Left_index);
                        if (iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            Log.d(TAG, "onCaptureError : " + error + " ErrorCode :" + errorCode);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                Log.d(TAG, "printState : " + error + " ErrorCode :" + error);
            //  printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };

    private CaptureResponder mCaptureResponseDefault4 = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            Log.d(TAG, "onCapture : Capture successful!");
            //  printState(getResources().getText(R.string.capture_single_ok));

            // log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.Right_Index);
                        if (iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            Log.d(TAG, "onCaptureError : " + error + " ErrorCode :" + errorCode);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                Log.d(TAG, "printState : " + error + " ErrorCode :" + error);
            //  printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };
    private CaptureResponder mCaptureResponseDefault5 = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            Log.d(TAG, "onCapture : Capture successful!");
            //  printState(getResources().getText(R.string.capture_single_ok));

            // log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.Left_middle);
                        if (iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            Log.d(TAG, "onCaptureError : " + error + " ErrorCode :" + errorCode);
            if (errorCode != IBioMiniDevice.ErrorCode.OK.value())
                Log.d(TAG, "printState : " + error + " ErrorCode :" + error);
            //  printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            if (mBioMiniFactory == null) return;
                            mBioMiniFactory.addDevice(device);
                            // log(Double.parseDouble(String.format(Locale.ENGLISH ,"Initialized device count- BioMiniFactory (%d)" , mBioMiniFactory.getDeviceCount() )));
                        }
                    } else {
                        Log.d(TAG, "permission denied : " + device);
                        // Log.d(TAG, "permission denied for device"+ device);
                    }
                }
            }
        }
    };

    public void checkDevice() {
        if (mUsbManager == null) return;
        Log.d(TAG, "checkDevice ");
        // log(Double.parseDouble("checkDevice"));
        HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIter = deviceList.values().iterator();
        while (deviceIter.hasNext()) {
            UsbDevice _device = deviceIter.next();
            if (_device.getVendorId() == 0x16d1) {
                //Suprema vendor ID
                mUsbManager.requestPermission(_device, mPermissionIntent);
            } else {
                Toast.makeText(mainContext, "something", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finger_print);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(FingerPrintActivity.this, R.color.colorPrimary));
        }

        FingerPrint.fingerPrintPower(1);
        Intent intent6 = new Intent("android.intent.action.set.finger.power");
        intent6.putExtra("finger_power_status", "on");
        sendBroadcast(intent6);

        mainContext = this;
        mCaptureOptionDefault.frameRate = IBioMiniDevice.FrameRate.SHIGH;
        AppCompatButton done = (AppCompatButton) findViewById(R.id.done);
        AppCompatButton exit = (AppCompatButton) findViewById(R.id.exit);

        done.setOnClickListener(v -> {

           // String Left_middleBase64Image = encodeToBase64(fingerBase64);
            Toast.makeText(mainContext, "I see you", Toast.LENGTH_SHORT).show();
            //apiFingerPrintUpload();
        });
        exit.setOnClickListener(v -> {
            startActivity(new Intent(getBaseContext(), HomeActivity.class));
            finish();
        });
        /* FingerPrint declaration*/
        findViewById(R.id.Left_thumb_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((ImageView) findViewById(R.id.Left_Thumb)).setImageBitmap(null);
                if (mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault,
                            true);
                }
            }
        });

        findViewById(R.id.right_thumb_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((ImageView) findViewById(R.id.right_Thumb)).setImageBitmap(null);
                if (mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault2,
                            true);
                }
            }
        });


        findViewById(R.id.left_index_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((ImageView) findViewById(R.id.Left_index)).setImageBitmap(null);
                if (mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault3,
                            true);
                }
            }
        });


        findViewById(R.id.right_index_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((ImageView) findViewById(R.id.Right_Index)).setImageBitmap(null);
                if (mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault4,
                            true);
                }
            }
        });

        findViewById(R.id.Left_middle_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // ((ImageView) findViewById(R.id.Left_middle)).setImageBitmap(null);
                if (mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault5,
                            true);
                }
            }
        });
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }

        restartBioMini();

    }


    void handleDevChange(IUsbEventHandler.DeviceChangeEvent event, Object dev) {
        if (event == IUsbEventHandler.DeviceChangeEvent.DEVICE_ATTACHED && mCurrentDevice == null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int cnt = 0;
                    while (mBioMiniFactory == null && cnt < 20) {
                        SystemClock.sleep(1000);
                        cnt++;
                    }
                    if (mBioMiniFactory != null) {
                        mCurrentDevice = mBioMiniFactory.getDevice(0);
                        //printState(getResources().getText(R.string.device_attached));
                        Log.d(TAG, "mCurrentDevice attached " + mCurrentDevice);
                        // Log.d(TAG, "mCurrentDevice attached : " + mCurrentDevice);
                        if (mCurrentDevice != null /*&& mCurrentDevice.getDeviceInfo() != null*/) {
                           /* log(Double.parseDouble(" DeviceName : " + mCurrentDevice.getDeviceInfo().deviceName));
                            log(Double.parseDouble("         SN : " + mCurrentDevice.getDeviceInfo().deviceSN));
                            log(Double.parseDouble("SDK version : " + mCurrentDevice.getDeviceInfo().versionSDK));*/
                        }
                    }
                }
            }).start();
        } else if (mCurrentDevice != null && event == IUsbEventHandler.DeviceChangeEvent.DEVICE_DETACHED && mCurrentDevice.isEqual(dev)) {
            //  printState(getResources().getText(R.string.device_detached));
            Log.d(TAG, "mCurrentDevice removed " + mCurrentDevice);
            // Log.d(TAG, "mCurrentDevice removed : " + mCurrentDevice);
            mCurrentDevice = null;
        }
    }

    void restartBioMini() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }
        if (mbUsbExternalUSBManager) {
            mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            mBioMiniFactory = new BioMiniFactory(mainContext, mUsbManager) {
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                    Log.d(TAG, "onDeviceChange : " + event + " using external usb-manager");
                    //  log(Double.parseDouble("----------------------------------------"));
                    //  log(Double.parseDouble("onDeviceChange : " + event + " using external usb-manager"));
                    // log(Double.parseDouble("----------------------------------------"));
                    handleDevChange(event, dev);
                }
            };
            //
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            checkDevice();
        } else {
            mBioMiniFactory = new BioMiniFactory(mainContext) {
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                    // log(Double.parseDouble("----------------------------------------"));
                    // log(Double.parseDouble("onDeviceChange : " + event));
                    Log.d(TAG, "onDeviceChange : " + event);
                    // log(Double.parseDouble("----------------------------------------"));
                    handleDevChange(event, dev);
                }
            };
        }
        //mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
    }


    @Override
    protected void onDestroy() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
            mBioMiniFactory = null;
        }
        if (mbUsbExternalUSBManager) {
            unregisterReceiver(mUsbReceiver);
        }

        FingerPrint.fingerPrintPower(0);
        Intent intent6 = new Intent("android.intent.action.set.finger.power");
        intent6.putExtra("finger_power_status", "off");
        sendBroadcast(intent6);
        super.onDestroy();
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mainContext, "permission granted", Toast.LENGTH_SHORT).show();
            //log(Double.parseDouble("permission granted"));
        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        requestPermission();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {

    }

    private String encodeToBase64(Bitmap imageBitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG,  100, outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);

    }

    public void saveLeftFingers() {
        if (mCurrentDevice != null) {
            final String userName = "Left_finger";
            byte[] wsq = mCurrentDevice.getCaptureImageAsWsq(-1, -1, 3.5f, 0);
            if (wsq == null) {
                Log.d(TAG, "Cannot get WSQ buffer");
                // log("<<ERROR>> Cannot get WSQ buffer");
                Toast.makeText(mainContext, "export_wsq_fail", Toast.LENGTH_SHORT).show();
                //  printState(getResources().getText(R.string.export_wsq_fail));
                return;
            }
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + userName + "_capturedImage.wsq");
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(wsq);
                fos.close();
                Toast.makeText(mainContext, "export_wsq_ok   "+wsq, Toast.LENGTH_SHORT).show();
                // printState(getResources().getText(R.string.export_wsq_ok));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void enrolMent() {
        if (mCurrentDevice != null) {
            final String userName = "left_thumb";
            ((ImageView) findViewById(R.id.Left_Thumb)).setImageBitmap(null);
            IBioMiniDevice.CaptureOption option = new IBioMiniDevice.CaptureOption();
            option.extractParam.captureTemplate = true;
            option.captureTemplate = true; //deprecated
            //option.frameRate = IBioMiniDevice.FrameRate.ELOW;
            // capture fingerprint image
            mCurrentDevice.captureSingle(option,
                    new CaptureResponder() {
                        @Override
                        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                                   final IBioMiniDevice.FingerState fingerState) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (capturedImage != null) {
                                        ImageView iv = (ImageView) findViewById(R.id.Left_Thumb);
                                        if (iv != null) {
                                            iv.setImageBitmap(capturedImage);
                                        }
                                    }
                                }
                            });
                            if (capturedTemplate != null) {
                                mUsers.add(new UserData(userName, capturedTemplate.data, capturedTemplate.data.length));
                                // log("User data added : " + userName);

                                // printState(getResources().getText(R.string.enroll_ok));
                            } else {
                                //  log("<<ERROR>> Template is not extracted...");
                                // printState(getResources().getText(R.string.enroll_fail));
                            }
                            // log(((IBioMiniDevice)context).popPerformanceLog());

                            return true;
                        }

                        @Override
                        public void onCaptureError(Object context, int errorCode, String error) {
                            // log("onCaptureError : " + error);
                            Log.d(TAG, "onCaptureError : " + error);
                            // printState(getResources().getText(R.string.enroll_fail));
                        }
                    }, true);
        }
    }


    private String convertJson1(Map<String, byte[]> dictionary) {
        JSONObject jsonObject = new JSONObject(dictionary);
        String jsonString = jsonObject.toString();
        return jsonString;
    }
    private String convertJson(Map<String, String> dictionary) {
        JSONObject jsonObject = new JSONObject(dictionary);
        String jsonString = jsonObject.toString();
        return jsonString;
    }

    private void apiFingerPrintUpload() {
        String dataImage = convertJson1(fingerprintImage);
        String imageBase64 = convertJson(fingerBase64);
        done.setVisibility(View.GONE);

        com.example.bepcom.network.ApiInterface api = com.example.bepcom.network.Api.CreateNodeApi();
        JsonObject object = new JsonObject();
        object.addProperty("file_number", com.example.bepcom.constant.Constant.fileNumber);
        object.addProperty("fingerprints", imageBase64);
        object.addProperty("fingerprint_images", dataImage);

        progressBar.setVisibility(View.VISIBLE);
        final Call<JsonObject> fingerprintModel = api.getFingerprint(object);

        fingerprintModel.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                Log.d(TAG,"finger"+response);
                Log.d(TAG,"finger"+imageBase64);
                Log.d(TAG,"finger"+dataImage);
                Log.d(TAG,"finger"+ com.example.bepcom.constant.Constant.fileNumber);
                if (response.isSuccessful() && response.errorBody() == null) {
                    if (response.code() == 200) {
                        Toast.makeText(FingerPrintActivity.this, "We are all good", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(getBaseContext(), HomeActivity.class));
                        finish();

                    } else {
                        Toast.makeText(mainContext, "Check if everything is okay! then try again", Toast.LENGTH_SHORT).show();

                    }


                }

                if (response.code() == 422) {
                    done.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(mainContext, "Something went wrong! please try again", Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                Toast.makeText(mainContext, "Something went wrong! please try again", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                Log.d("Throwable  ------- > ", t.toString());
                done.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}