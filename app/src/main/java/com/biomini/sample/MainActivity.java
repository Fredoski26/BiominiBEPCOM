package com.biomini.sample;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.suprema.BioMiniFactory;
import com.suprema.CaptureResponder;
import com.suprema.IBioMiniDevice;
import com.suprema.IUsbEventHandler;
import com.telpo.tps550.api.fingerprint.FingerPrint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Flag.
    public static final boolean mbUsbExternalUSBManager = false;
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private UsbManager mUsbManager = null;
    private PendingIntent mPermissionIntent= null;
    //


    private static BioMiniFactory mBioMiniFactory = null;
    public static final int REQUEST_WRITE_PERMISSION = 786;
    public IBioMiniDevice mCurrentDevice = null;
    private MainActivity mainContext;

    public final static String TAG = "BioMini Sample";
    private EditText mLogView;
    private ScrollView mScrollLog = null;

    private ViewPager mPager;

    private int []mNaviPicks= { R.id.pageindexImage_0 , R.id.pageindexImage_1 , R.id.pageindexImage_2, R.id.pageindexImage_3};

    int nInfComponents [] = {R.id.editLog, R.id.scrollLog ,
            R.id.seekBarSensitivity , R.id.seekBarSecurityLevel , R.id.seekBarTimeout , R.id.checkBoxFastMode , R.id.checkBoxCropMode, R.id.buttonReadCaptureParam , R.id.buttonWriteCaptureParam,
            R.id.buttonEnroll , R.id.buttonVerify , R.id.buttonDeleteAll ,
            R.id.buttonExportBmp, R.id.buttonExportWsq , R.id.buttonTemplate, R.id.button19794_4};

    int nLayouts[] = { R.layout.log_view , R.layout.setting_capture , R.layout.enrollment , R.layout.export};
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
    private CaptureResponder mCaptureResponseDefault = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {
            log("onCapture : Capture successful!");
            printState(getResources().getText(R.string.capture_single_ok));

            log(((IBioMiniDevice) context).popPerformanceLog());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                        if(iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object contest, int errorCode, String error) {
            log("onCaptureError : " + error + " ErrorCode :" + errorCode);
            if( errorCode != IBioMiniDevice.ErrorCode.OK.value())
                printState(getResources().getText(R.string.capture_single_fail) + "("+error+")");
        }
    };
    private CaptureResponder mCaptureResponsePrev = new CaptureResponder() {
        @Override
        public boolean onCaptureEx(final Object context, final Bitmap capturedImage,
                                   final IBioMiniDevice.TemplateData capturedTemplate,
                                   final IBioMiniDevice.FingerState fingerState) {

            Log.d("CaptureResponsePrev", String.format(Locale.ENGLISH , "captureTemplate.size (%d) , fingerState(%s)" , capturedTemplate== null? 0 : capturedTemplate.data.length, String.valueOf(fingerState.isFingerExist)));
            printState(getResources().getText(R.string.start_capture_ok));
            byte[] pImage_raw =null;
            if( (mCurrentDevice!= null && (pImage_raw = mCurrentDevice.getCaptureImageAsRAW_8() )!= null)) {
                Log.d("CaptureResponsePrev ", String.format(Locale.ENGLISH, "pImage (%d) , FP Quality(%d)", pImage_raw.length , mCurrentDevice.getFPQuality(pImage_raw, mCurrentDevice.getImageWidth(), mCurrentDevice.getImageHeight(), 2)));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(capturedImage != null) {
                        ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                        if(iv != null) {
                            iv.setImageBitmap(capturedImage);
                        }
                    }
                }
            });
            return true;
        }

        @Override
        public void onCaptureError(Object context, int errorCode, String error) {
            log("onCaptureError : " + error);
            log(((IBioMiniDevice)context).popPerformanceLog());
            if( errorCode != IBioMiniDevice.ErrorCode.OK.value())
                printState(getResources().getText(R.string.start_capture_fail));
        }
    };
    synchronized public void printState(final CharSequence str){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.textStatus)).setText(str);
            }
        });

    }
    synchronized public void log(final String msg)
    {
        Log.d(TAG, msg);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if( mLogView == null){
                    mLogView = (EditText) findViewById(R.id.editLog );
                }
                if(mLogView != null) {
                    mLogView.append(msg + "\n");
                    if(mScrollLog != null) {
                        mScrollLog.fullScroll(mScrollLog.FOCUS_DOWN);
                    }else{
                        Log.d("Log " , "ScrollView is null");
                    }
                }
                else {
                    Log.d("", msg);
                }
            }
        });
    }

    synchronized public void printRev(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView) findViewById(R.id.revText)).setText(msg);
            }
        });
    }


    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action)){
                synchronized(this){
                    UsbDevice device = (UsbDevice)intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)){
                        if(device != null){
                            if( mBioMiniFactory == null) return;
                            mBioMiniFactory.addDevice(device);
                            log(String.format(Locale.ENGLISH ,"Initialized device count- BioMiniFactory (%d)" , mBioMiniFactory.getDeviceCount() ));
                        }
                    }
                    else{
                        Log.d(TAG, "permission denied for device"+ device);
                    }
                }
            }
        }
    };
    public void checkDevice(){
        if(mUsbManager == null) return;
        log("checkDevice");
        HashMap<String , UsbDevice> deviceList = mUsbManager.getDeviceList();
        Iterator<UsbDevice> deviceIter = deviceList.values().iterator();
        while(deviceIter.hasNext()){
            UsbDevice _device = deviceIter.next();
            if( _device.getVendorId() ==0x16d1 ){
                //Suprema vendor ID
                mUsbManager.requestPermission(_device , mPermissionIntent);
            }else{
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FingerPrint.fingerPrintPower(1);

        Intent intent6 = new Intent("android.intent.action.set.finger.power");
        intent6.putExtra("finger_power_status","on");
        sendBroadcast(intent6);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Auto generated above

        mainContext = this;

        mCaptureOptionDefault.frameRate = IBioMiniDevice.FrameRate.SHIGH;

        findViewById(R.id.buttonCaptureSingle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) findViewById(R.id.imagePreview)).setImageBitmap(null);
                if(mCurrentDevice != null) {
                    //mCaptureOptionDefault.captureTimeout = (int)mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                    mCurrentDevice.captureSingle(
                            mCaptureOptionDefault,
                            mCaptureResponseDefault,
                            true);
                }
            }
        });
        findViewById(R.id.buttonStartCapturing).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCurrentDevice != null) {
                    BioMiniFactory mBioMiniFactory = new BioMiniFactory(getApplicationContext()) {
                        @Override
                        public void onDeviceChange(DeviceChangeEvent event, Object dev) {

                        }
                    };
                    IBioMiniDevice mCurrentDeivce = null;
                    // Make BioMiniFactory instance, and get device handler(IBioMiniDevice).
                    //mCaptureOptionDefault.captureTemplate =true;
                    mCaptureOptionDefault.captureImage=true;
                    //mCaptureOptionDefault.frameRate = IBioMiniDevice.FrameRate.ELOW;
                    mCurrentDevice.startCapturing(
                            mCaptureOptionDefault,
                            mCaptureResponsePrev);
                }
            }
        });

        findViewById(R.id.buttonAbortCapturing).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mCurrentDevice != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            mCurrentDevice.abortCapturing();
                            int nRetryCount =0;
                            while(mCurrentDevice != null && mCurrentDevice.isCapturing()){
                                SystemClock.sleep(10);
                                nRetryCount++;
                            }
                            Log.d("AbortCapturing" , String.format(Locale.ENGLISH ,
                                    "IsCapturing return false.(Abort-lead time: %dms) " ,
                                    nRetryCount* 10));
                        }
                    }).start();
                }
            }
        });

        if(mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }

        if( !mbUsbExternalUSBManager ){
            Button btn_checkDevice = (Button)findViewById(R.id.buttonCheckDevice);
            btn_checkDevice.setClickable(false);
            btn_checkDevice.setEnabled(false);
        }else{
            ((Button)findViewById(R.id.buttonCheckDevice)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkDevice();
                }
            });
        }

        restartBioMini();

        printRev(""+mBioMiniFactory.getSDKInfo());

        mPager = (ViewPager)findViewById(R.id.viewpager);
        PageAdaptor viewPageradaptor= new PageAdaptor(getLayoutInflater());
        mPager.setAdapter(viewPageradaptor);
        mPager.setOffscreenPageLimit(5);
        //
        mScrollLog = (ScrollView) findViewById(R.id.scrollLog);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {


            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setMenuPicker(position);
            }

            @Override
            public void onPageSelected(int position) {
                bindComponents();
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        navigationView.getMenu().getItem(0).setChecked(true);
        bindComponents();
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
                        printState(getResources().getText(R.string.device_attached));
                        Log.d(TAG, "mCurrentDevice attached : " + mCurrentDevice);
                        if (mCurrentDevice != null /*&& mCurrentDevice.getDeviceInfo() != null*/) {
                            log(" DeviceName : " + mCurrentDevice.getDeviceInfo().deviceName);
                            log("         SN : " + mCurrentDevice.getDeviceInfo().deviceSN);
                            log("SDK version : " + mCurrentDevice.getDeviceInfo().versionSDK);
                        }
                    }
                }
            }).start();
        } else if (mCurrentDevice != null && event == IUsbEventHandler.DeviceChangeEvent.DEVICE_DETACHED && mCurrentDevice.isEqual(dev)) {
            printState(getResources().getText(R.string.device_detached));
            Log.d(TAG, "mCurrentDevice removed : " + mCurrentDevice);
            mCurrentDevice = null;
        }
    }

    void restartBioMini() {
        if(mBioMiniFactory != null) {
            mBioMiniFactory.close();
        }
        if( mbUsbExternalUSBManager ){
            mUsbManager = (UsbManager)getSystemService(Context.USB_SERVICE);
            mBioMiniFactory = new BioMiniFactory(mainContext, mUsbManager){
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                    log("----------------------------------------");
                    log("onDeviceChange : " + event + " using external usb-manager");
                    log("----------------------------------------");
                    handleDevChange(event, dev);
                }
            };
            //
            mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE);
            IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
            registerReceiver(mUsbReceiver, filter);
            checkDevice();
        }else {
            mBioMiniFactory = new BioMiniFactory(mainContext) {
                @Override
                public void onDeviceChange(DeviceChangeEvent event, Object dev) {
                    log("----------------------------------------");
                    log("onDeviceChange : " + event);
                    log("----------------------------------------");
                    handleDevChange(event, dev);
                }
            };
        }
        //mBioMiniFactory.setTransferMode(IBioMiniDevice.TransferMode.MODE2);
    }

    @Override
    protected void onPostResume() {
        bindComponents();
        super.onPostResume();
    }

    public void bindComponents(){
        for( int i=0 ; i< nInfComponents.length ; i++){
            View v = findViewById(nInfComponents[i]);
            if( v instanceof Button){
                if( !((Button)v).hasOnClickListeners()){
                    v.setOnClickListener(ClickEvent);
                }
            }else if( v instanceof  EditText && v.getId() == R.id.editLog) {
                mLogView = (EditText) findViewById(R.id.editLog);
            }else if( v instanceof  ScrollView && v.getId() == R.id.scrollLog){
                mScrollLog = (ScrollView)findViewById(R.id.scrollLog);
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mBioMiniFactory != null) {
            mBioMiniFactory.close();
            mBioMiniFactory = null;
        }
        if( mbUsbExternalUSBManager ){
            unregisterReceiver(mUsbReceiver);
        }

        FingerPrint.fingerPrintPower(0);
        Intent intent6 = new Intent("android.intent.action.set.finger.power");
        intent6.putExtra("finger_power_status","off");
        sendBroadcast(intent6);
        super.onDestroy();
    }

    public void setMenuPicker(int idx){
        if( idx >mPager.getChildCount()){return ;}
        for( int i =0 ; i < mNaviPicks.length ; i++){
            ImageView img_view = (ImageView)findViewById(mNaviPicks[i]);
            if(idx == i ){
                img_view.setImageResource(R.drawable.ic_place_grey600_48dp);
            }else{
                img_view.setImageResource(R.drawable.ic_pin_drop_grey_underbar);
            }
        }
    }
    public void clearState(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.textStatus)).clearComposingText();
            }
        });

    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},  REQUEST_WRITE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            log("permission granted");
        }
    }
    @Override
    public void onPostCreate(Bundle savedInstanceState){
        requestPermission();
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        //}

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if( mPager == null ) return false;

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        if (id == R.id.nav_logview) {
            mPager.setCurrentItem(0);
        } else if (id == R.id.nav_capture_settings) {
            mPager.setCurrentItem(1);
        } else if (id == R.id.nav_enrollment) {
            mPager.setCurrentItem(2);
        } else if (id == R.id.nav_export) {
            mPager.setCurrentItem(3);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class  PageAdaptor extends PagerAdapter {

        LayoutInflater infalter ;
        public PageAdaptor(LayoutInflater inf) {this.infalter = inf;}
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = infalter.inflate(nLayouts[position], null);
            container.addView(view);
            return view;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public int getCount() {
            return nLayouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    // OnClick Event .
    View.OnClickListener ClickEvent = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId() ){
                case R.id.buttonEnroll:
                    if (mCurrentDevice != null) {
                        final String userName = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                        if (userName.equals("")) {
                            log("<<ERROR>> There is no user name");
                            return;
                        }
                        ((ImageView) findViewById(R.id.imagePreview)).setImageBitmap(null);
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
                                                if(capturedImage != null) {
                                                    ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                                                    if(iv != null) {
                                                        iv.setImageBitmap(capturedImage);
                                                    }
                                                }
                                            }
                                        });
                                        if(capturedTemplate != null) {
                                            mUsers.add(new UserData(userName, capturedTemplate.data, capturedTemplate.data.length));
                                            log("User data added : " + userName);
                                            printState(getResources().getText(R.string.enroll_ok));
                                        }
                                        else {
                                            log("<<ERROR>> Template is not extracted...");
                                            printState(getResources().getText(R.string.enroll_fail));
                                        }
                                        log(((IBioMiniDevice)context).popPerformanceLog());

                                        return true;
                                    }

                                    @Override
                                    public void onCaptureError(Object context, int errorCode, String error) {
                                        log("onCaptureError : " + error);
                                        printState(getResources().getText(R.string.enroll_fail));
                                    }
                                }, true);
                    }
                    break;
                case R.id.buttonVerify:
                    if (mCurrentDevice != null) {
                        if (mUsers.size() == 0) {
                            log("There is no enrolled data");
                            return;
                        }
                        // capture fingerprint image
                        IBioMiniDevice.CaptureOption option = new IBioMiniDevice.CaptureOption();
                        option.extractParam.captureTemplate = true;
                        option.captureTemplate = true; //deprecated

                        if (mCurrentDevice.getDeviceInfo().scannerType.getDeviceClass() == IBioMiniDevice.ScannerClass.UNIVERSIAL_DEVICE) {
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
                                                        ImageView iv = (ImageView) findViewById(R.id.imagePreview);
                                                        if (iv != null) {
                                                            iv.setImageBitmap(capturedImage);
                                                        }
                                                    }
                                                }
                                            });
                                            if (capturedTemplate != null) {
                                                boolean isMatched = false;
                                                String matchedName = "";
                                                for (UserData ud : mUsers) {
                                                    if (mCurrentDevice.verify(
                                                            capturedTemplate.data, capturedTemplate.data.length,
                                                            ud.template, ud.template.length)) {
                                                        isMatched = true;
                                                        matchedName = ud.name;
                                                        break;
                                                    }
                                                }
                                                if (isMatched) {
                                                    log("Match found : " + matchedName);
                                                    printState(getResources().getText(R.string.verify_ok));
                                                } else {
                                                    log("No match found : ");
                                                    printState(getResources().getText(R.string.verify_not_match));
                                                }
                                            } else {
                                                log("<<ERROR>> Template is not extracted...");
                                                printState(getResources().getText(R.string.verify_fail));
                                            }
                                            return true;
                                        }

                                        @Override
                                        public void onCaptureError(Object context, int errorCode, String error) {
                                            log("onCaptureError : " + error);
                                            printState(getResources().getText(R.string.capture_fail));
                                        }
                                    }, true);
                        }else if (mCurrentDevice.getDeviceInfo().scannerType.getDeviceClass() == IBioMiniDevice.ScannerClass.HID_DEVICE) {

                            UserData _user = mUsers.get(mUsers.size()-1);
                            if(mCurrentDevice.verify(_user.template , null)) { // HID Device does not supported verify with two templates.
                                log("Match found : " + _user.name);
                                printState(getResources().getText(R.string.verify_ok));
                            }else {
                                IBioMiniDevice.ErrorCode _ecode =  mCurrentDevice.getLastError();
                                log(_ecode.toString()  + "("+_ecode.value()  +")");
                                printState(getResources().getText(R.string.verify_not_match));
                            }
                        }
                    }
                    break;
                case R.id.buttonDeleteAll: {
                    mUsers.clear();
                    printState(getResources().getText(R.string.delete_user));
                }
                break;
                case R.id.buttonExportBmp:
                {
                    if (mCurrentDevice != null) {
                        final String userName = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                        if (userName.equals("")) {
                            log("<<ERROR>> There is no user name");
                            printState(getResources().getText(R.string.no_user_name));
                            return;
                        }
                        byte[] bmp = mCurrentDevice.getCaptureImageAsBmp();
                        if (bmp == null) {
                            log("<<ERROR>> Cannot get BMP buffer");
                            printState(getResources().getText(R.string.export_bmp_fail));
                            return;
                        }
                        try {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + userName + "_capturedImage.bmp");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(bmp);
                            fos.close();

                            printState(getResources().getText(R.string.export_bmp_ok));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
                case R.id.buttonExportWsq: {
                    if (mCurrentDevice != null) {
                        final String userName = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                        if (userName.equals("")) {
                            log("<<ERROR>> There is no user name");
                            printState(getResources().getText(R.string.no_user_name));
                            return;
                        }

                        byte[] wsq = mCurrentDevice.getCaptureImageAsWsq(-1, -1, 3.5f, 0);
                        if (wsq == null) {
                            log("<<ERROR>> Cannot get WSQ buffer");
                            printState(getResources().getText(R.string.export_wsq_fail));
                            return;
                        }
                        try {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + userName + "_capturedImage.wsq");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(wsq);
                            fos.close();
                            printState(getResources().getText(R.string.export_wsq_ok));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
                case R.id.buttonTemplate: {
                    if (mCurrentDevice != null) {
                        final String userName = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                        if (userName.equals("")) {
                            log("<<ERROR>> There is no user name");
                            printState(getResources().getText(R.string.no_user_name));
                            return;
                        }

                        int tmp_type_idx = (int) ((Spinner) findViewById(R.id.spinnerTemplateType)).getSelectedItemId();
                        int tmp_type;
                        IBioMiniDevice.TemplateType _type;
                        String encKey = ((EditText) findViewById(R.id.editEncryptKey)).getText().toString();
                        if (encKey.equals("")) {
                            mCurrentDevice.setEncryptionKey(null);
                        } else {
                            try {
                                mCurrentDevice.setEncryptionKey(encKey.getBytes("UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                                mCurrentDevice.setEncryptionKey(null);
                                log("<<ERROR>> cannot set encryption key unsupported character sets assigned...");
                                printState(getResources().getText(R.string.export_template_fail));
                                e.printStackTrace();
                            }
                        }
                        switch (tmp_type_idx) {
                            case 0:
                                tmp_type = IBioMiniDevice.TemplateType.SUPREMA.value();
                                _type = IBioMiniDevice.TemplateType.SUPREMA;
                                break;
                            case 1:
                                tmp_type = IBioMiniDevice.TemplateType.ISO19794_2.value();
                                _type = IBioMiniDevice.TemplateType.ISO19794_2;
                                break;
                            case 2:
                                tmp_type = IBioMiniDevice.TemplateType.ANSI378.value();
                                _type = IBioMiniDevice.TemplateType.ANSI378;
                                break;
                            default:
                                tmp_type = IBioMiniDevice.TemplateType.SUPREMA.value();
                                _type = IBioMiniDevice.TemplateType.SUPREMA;
                                break;
                        }
                        mCurrentDevice.setParameter(
                                new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TEMPLATE_TYPE,
                                        tmp_type));
                        IBioMiniDevice.TemplateData tmp = mCurrentDevice.extractTemplate();
                        if (tmp == null) {
                            log("<<ERROR>> Cannot get Template buffer");
                            printState(getResources().getText(R.string.export_template_fail));
                            return;
                        }
                        if (tmp.data != null) {
                            try {

                                File file = new File(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +
                                                userName + "_(" + _type.toString() + ")_capturedTemplate.tmp");
                                FileOutputStream fos = new FileOutputStream(file);
                                fos.write(tmp.data);
                                fos.close();
                                printState(getResources().getText(R.string.export_template_ok));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } finally {
                                try {
                                    if (!encKey.equals("")) {
                                        File file = new File(
                                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" +
                                                        userName + "_(" + _type.toString() + " _capturedTemplate_dec.tmp");
                                        FileOutputStream fos = new FileOutputStream(file);
                                        fos.write(mCurrentDevice.decrypt(tmp.data));
                                        fos.close();
                                        printState(getResources().getText(R.string.export_template_ok));

                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
                break;
                case R.id.button19794_4: {
                    if (mCurrentDevice != null) {
                        final String userName = ((EditText) findViewById(R.id.editUsername)).getText().toString();
                        if (userName.equals("")) {
                            log("<<ERROR>> There is no user name");
                            printState(getResources().getText(R.string.no_user_name));
                            return;
                        }
                        byte[] format19794_4 = mCurrentDevice.getCaptureImageAs19794_4();
                        if (format19794_4 == null) {
                            log("<<ERROR>> Cannot get 19794_4 buffer");
                            printState(getResources().getText(R.string.export_19794_4_fail));
                            return;
                        }
                        try {
                            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + userName + "_capturedImage.dat");
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(format19794_4);
                            fos.close();
                            printState(getResources().getText(R.string.export_19794_4_ok));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
                case R.id.buttonReadCaptureParam:
                    if(mCurrentDevice != null) {
                        int security_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL).value;
                        int sensitivity_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SENSITIVITY).value;
                        int timeout = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.TIMEOUT).value;
                        Log.e("yw","timeout"+timeout);
                        int lfd_level = (int) mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.DETECT_FAKE).value;
                        boolean fast_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.FAST_MODE).value == 1;
                        boolean crop_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.SCANNING_MODE).value == 1;
                        boolean ext_trigger = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.EXT_TRIGGER).value == 1;
                        boolean auto_sleep = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.ENABLE_AUTOSLEEP).value == 1;
                        boolean extract_mode = mCurrentDevice.getParameter(IBioMiniDevice.ParameterType.EXTRACT_MODE_BIOSTAR).value == 1;
                        ((SeekBar) findViewById(R.id.seekBarSecurityLevel)).setProgress(security_level);
                        ((SeekBar) findViewById(R.id.seekBarSensitivity)).setProgress(sensitivity_level);
                        ((SeekBar) findViewById(R.id.seekBarTimeout)).setProgress(timeout/1000);
                        ((SeekBar) findViewById(R.id.seekBarLfdLevel)).setProgress(lfd_level);
                        ((CheckBox) findViewById(R.id.checkBoxFastMode)).setChecked(fast_mode);
                        ((CheckBox) findViewById(R.id.checkBoxCropMode)).setChecked(crop_mode);
                        ((CheckBox) findViewById(R.id.checkBoxExtTrigger)).setChecked(ext_trigger);
                        ((CheckBox) findViewById(R.id.checkBoxAutoSleep)).setChecked(auto_sleep);
                        printState(getResources().getText(R.string.read_params_ok));
                    }

                    break;
                case R.id.buttonWriteCaptureParam:

                    if(mCurrentDevice != null) {
                        int security_level = ((SeekBar) findViewById(R.id.seekBarSecurityLevel)).getProgress();
                        int sensitivity_level = ((SeekBar) findViewById(R.id.seekBarSensitivity)).getProgress();
                        int timeout = ((SeekBar) findViewById(R.id.seekBarTimeout)).getProgress();
                        Log.e("yw","timeout1    "+timeout);
                        int lfd_level = ((SeekBar) findViewById(R.id.seekBarLfdLevel)).getProgress();
                        boolean fast_mode = ((CheckBox) findViewById(R.id.checkBoxFastMode)).isChecked();
                        boolean crop_mode = ((CheckBox) findViewById(R.id.checkBoxCropMode)).isChecked();
                        boolean ext_trigger = ((CheckBox) findViewById(R.id.checkBoxExtTrigger)).isChecked();
                        boolean auto_sleep = ((CheckBox) findViewById(R.id.checkBoxAutoSleep)).isChecked();
                        boolean extract_mode = false;
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SECURITY_LEVEL, security_level));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SENSITIVITY, sensitivity_level));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.TIMEOUT, timeout*1000));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.DETECT_FAKE, lfd_level));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.FAST_MODE, fast_mode?1:0));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.SCANNING_MODE, crop_mode?1:0));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.EXT_TRIGGER, ext_trigger?1:0));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.ENABLE_AUTOSLEEP, auto_sleep?1:0));
                        mCurrentDevice.setParameter(new IBioMiniDevice.Parameter(IBioMiniDevice.ParameterType.EXTRACT_MODE_BIOSTAR, extract_mode?1:0));
                        printState(getResources().getText(R.string.write_params_ok));
                    }
                    break;
            }
        }
    };
}