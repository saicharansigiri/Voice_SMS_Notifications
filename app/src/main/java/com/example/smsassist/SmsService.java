package com.example.smsassist;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

import static com.example.smsassist.App.CHANNEL_ID;
import static com.example.smsassist.MainActivity.PITCH_KEY_NAME;
import static com.example.smsassist.MainActivity.SHARED_PREF_NAME;
import static com.example.smsassist.MainActivity.SPEED_KEY_NAME;

public class SmsService extends Service {
    public static SmsService instance;
    private TextToSpeech sTTs;
    private int pitch;
    private int speed;
    public static SmsService getInstance(){
        return instance;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = SmsService.this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent notificationIntent = new Intent(this,MainActivity.class);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

          pitch = sharedPreferences.getInt(PITCH_KEY_NAME,50);
         speed = sharedPreferences.getInt(SPEED_KEY_NAME,50);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0,notificationIntent,0);
        Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setContentTitle("Voice Notifications are Turned on")
                .setContentText("Tap to Turn off")
                .setSmallIcon(R.drawable.ic_baseline_record_voice_over_24)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1,notification);
        sTTs = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = sTTs.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("mTTs","LANGUAGE NOT SUPPORTED");
                    }
                    else{
                        Log.e("TTSok","TTS_WORKING");
                    }
                }
                else{
                    Log.e("mTTS","INIT FAILED");
                }
            }
        });
        return START_NOT_STICKY;

    }
    public void speak(String msg){
        float pitchValue = (float) pitch/50;
        if(pitch<0.1) pitchValue = 0.1f;
        float speedValue = (float) speed/50;
        if(speed<0.1) speedValue = 0.1f;
        sTTs.setPitch(pitchValue);
        sTTs.setSpeechRate(speedValue);
        sTTs.speak(msg,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(sTTs!=null){
            sTTs.stop();
            sTTs.shutdown();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
