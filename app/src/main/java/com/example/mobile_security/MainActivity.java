package com.example.mobile_security;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private Button main_BTN_login;
    private EditText main_LBL_name;
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        findView();

        main_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCondition();
            }
        });
    }

    private void checkCondition() {
        String input = main_LBL_name.getText().toString();

        if (batteryLevel(input) || checkPhoneNumber(input) || isLandscape()|| isRingerModeOn() || ("" + currentBrightness()).equals(input)) {
            openNewActivity();
        }

        ifLocationInDizengoffSquareTelAvivYafo();
    }

    // Checks the percentage of battery in the phone
    private boolean batteryLevel(String input) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        return input.contains(batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)+"");
    }

    // Checks the current brightness in the phone
    private float currentBrightness() {
        float currentBrightnessVal = 0;
        try {
            currentBrightnessVal = android.provider.Settings.System.getInt(
                    getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return currentBrightnessVal;
    }

    // Get the amount of contacts in the phone
    private int totalContacts() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);

        return cursor.getCount();
    }

    // Checks if the phone number appears in the contacts
    private boolean checkPhoneNumber(String phoneNumber) {

        if(phoneNumber.isEmpty()) {
            return false;
        }

        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
        Cursor cur = getApplicationContext().getContentResolver().query(lookupUri,mPhoneNumberProjection, null, null, null);

        return cur.moveToFirst();
    }

    //Check the location of the phone
    @SuppressLint("MissingPermission")
    private void ifLocationInDizengoffSquareTelAvivYafo() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MainActivity.this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, MainActivity.this);

    }

    private void findView() {
        main_LBL_name = findViewById(R.id.main_LBL_name);
        main_BTN_login = findViewById(R.id.main_BTN_login);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.READ_CONTACTS
            }, 100);
        }
    }
    //Return true if device is on ringer mode
    public boolean isRingerModeOn() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        return audioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    //Return true if device is on ringer mode
    public boolean isVolumeMax(){
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return currentVolume == maxVolume;
    }

    public boolean isLandscape() {
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE)
            return true;
        else
            return false;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        //location in in Dizengoff Square, Tel Aviv-Yafo
        if(latitude == 32.0779583 && longitude == 34.7742048) {
            openNewActivity();
        }
    }

    private void openNewActivity() {
        Intent intent = new Intent(MainActivity.this, New_Page.class);
        startActivity(intent);
    }
}