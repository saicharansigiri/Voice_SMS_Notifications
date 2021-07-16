package com.example.smsassist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    private static  final String SMS_RECEIVED =  "android.provider.Telephony.SMS_RECEIVED";
    //TODO:Implement whats app text to speech
    public static final String WHATS_APP_RECEIVED = "";
    private static final String TAG = "SmsBroadcastReceiver";
    String msg,PhoneNo = "";


    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG,"INTENT RECEIVED:"+intent.getAction());
        //if(intent.getAction()==SMS_RECEIVED){
          if(intent.getAction().equals(SMS_RECEIVED)){
            //Retrieve  a map of extended data from the intent
            Bundle dataBundle = intent.getExtras();
            if(dataBundle!=null){
                //creating a PDU (PROTOCOL DATA UNIT)
                Object[]  mypdu = (Object[]) dataBundle.get("pdus");
                final SmsMessage[] message = new SmsMessage[mypdu.length];
                for(int i=0;i<mypdu.length;i++){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                        //API leve  > than 23
                        String format = dataBundle.getString("format");
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i],format);
                    }
                    else{
                        message[i] = SmsMessage.createFromPdu((byte[])mypdu[i]);
                    }
                    msg = message[i].getMessageBody();
                    PhoneNo = message[i].getOriginatingAddress();
                }

                SmsService instance = SmsService.getInstance();
                //if startservice button is not pressed then if we get a message then instance will be null so that's why we need to check for null
                if(instance!=null){
                    instance.speak(msg);
                }
                Toast.makeText(context, "Message: "+msg+" \nNumber: "+PhoneNo, Toast.LENGTH_SHORT).show();


            }
            else{
                Toast.makeText(context, "Data bundle is null", Toast.LENGTH_SHORT).show();
            }
        }
          else {
              Log.e("SMSNOTRECEIVED","SMSNOTRECEIVED");
          }
    }
}