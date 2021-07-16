package com.example.smsassist;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    SeekBar seekBarPitch;
    SeekBar seekBarSpeed;
    TextView showPitchValue,showSpeedValue;
    Button startService;
    Button stopService;
    ImageButton pitchDBtn,pitchIBtn,speedDBtn,speedIBtn;

    public static final String SHARED_PREF_NAME = "ttsValues";
    public static final String PITCH_KEY_NAME = "key_Pitch";
    public static final String SPEED_KEY_NAME = "key_Speed";
    public static final String SERVICE_STATE_KEY = "is_start_service_state";

    private static  final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 0;
    private MyReceiver receiver;


    @Override
    protected void onStart() {
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiver = new MyReceiver();
        registerReceiver(receiver,intentFilter);
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        seekBarPitch = findViewById(R.id.seekbar_pitch);
        seekBarSpeed = findViewById(R.id.seekbar_speed);


        startService = findViewById(R.id.startService);
        stopService = findViewById(R.id.stopService);

        showPitchValue = findViewById(R.id.pitchValue);
        showSpeedValue = findViewById(R.id.speedValue);
        pitchDBtn = findViewById(R.id.pitchDBtn);
        pitchIBtn = findViewById(R.id.pitchIBtn);
        speedDBtn = findViewById(R.id.speedDBtn);
        speedIBtn = findViewById(R.id.speedIBtn);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        //getting seekbar values from sharedPreferences and showing them
        seekBarPitch.setProgress(sharedPreferences.getInt(PITCH_KEY_NAME,50));
        showPitchValue.setText("Pitch: "+sharedPreferences.getInt(PITCH_KEY_NAME,50));

        seekBarSpeed.setProgress(sharedPreferences.getInt(SPEED_KEY_NAME,50));
        showSpeedValue.setText("Speed: "+sharedPreferences.getInt(SPEED_KEY_NAME,50));

        SharedPreferences.Editor editor = sharedPreferences.edit();
        //pitch  decrease btn
        pitchDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = seekBarPitch.getProgress();
                seekBarPitch.setProgress(--value);
                UpdateSharedPrefOfPitch(editor,value);
            }
        });
        //pitch increase
        pitchIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1 = seekBarPitch.getProgress();
                seekBarPitch.setProgress(++value1);
                UpdateSharedPrefOfPitch(editor,value1);
            }
        });

        //speed  decrease btn
        speedDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = seekBarSpeed.getProgress();
                seekBarSpeed.setProgress(--value);
                UpdateSharedPrefOfSpeed(editor,value);
            }
        });
        //speed increase btn
        speedIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value1 = seekBarSpeed.getProgress();
                seekBarSpeed.setProgress(++value1);
                UpdateSharedPrefOfSpeed(editor,value1);
            }
        });

        seekBarPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt(PITCH_KEY_NAME,progress);
                editor.apply();
                showPitchValue.setText("Pitch: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                editor.putInt(SPEED_KEY_NAME,progress);
                editor.apply();
                showSpeedValue.setText("Speed: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        startService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService.setEnabled(false);
                stopService.setEnabled(true);
                startSmsService();

            }
        });

        stopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService.setEnabled(true);
               stopService.setEnabled(false);
                stopSmsService();
            }
        });

        //check use permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
            // if Permission is not Granted then chek if user has denied the permission
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.RECEIVE_SMS)){
                 //Do nothing as user has denied Permission
            }
            else{
                //a pop up will appear for asking permission
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.READ_SMS},MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }//OnCreate

    private void UpdateSharedPrefOfPitch(SharedPreferences.Editor editor,int value){
        editor.putInt(PITCH_KEY_NAME,value);
        editor.apply();
    }
    private void UpdateSharedPrefOfSpeed(SharedPreferences.Editor editor,int value){
        editor.putInt(SPEED_KEY_NAME,value);
        editor.apply();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECEIVE_SMS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Super", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "To make Work this app Work Allow  SMS Permisssion!!", Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }


    public void startSmsService(){

        Intent serviceIntent = new Intent(this,SmsService.class);
        startService(serviceIntent);
    }


    public void stopSmsService(){
        Intent stopIntent = new Intent(this,SmsService.class);
        Log.i("SOMETAGGGGGGGGGGG", String.valueOf((receiver==null)));
        stopService(stopIntent);
        Log.i("SOMETAGGGGGGGGGGG", String.valueOf((receiver==null)));
        if (!(receiver == null)) {
            unregisterReceiver(receiver);
            receiver = null;
            Log.i("SSSSSSSSSSSSSSSSSS","ITS UNREGESTRED");
        }
    }


}


