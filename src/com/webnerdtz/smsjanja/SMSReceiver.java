package com.webnerdtz.smsjanja;

import java.text.DateFormat;
import java.util.Date;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.webnerdtz.smsjanja.database.MessageTable;
import com.webnerdtz.smsjanja.janjacontentprovider.JanjaContentProvider;


public class SMSReceiver extends BroadcastReceiver
{
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if(intent.getAction().equals(SMS_RECEIVED)){
		// get the sms message passed in
		Log.d("Janja Service:", "Receiving message...");
		Bundle bundle = intent.getExtras();
		SmsMessage[] msgs = null;
		if(bundle != null)
		{
			// retrieve the message received
			Object[] pdus = (Object[]) bundle.get("pdus");
			if(pdus.length == 0){
				return;
			}
			msgs = new SmsMessage[pdus.length];
			
			String sender = "";
			String message = "";
			String timestamp = "";
			String datetime = "";
			
			Intent smsReplyIntent = new Intent(context, JsonRemoteService.class);
			smsReplyIntent.setAction("SMS_RECEIVED_FOR_REMOTE_SERVICE");
			
			StringBuilder content = new StringBuilder();
			
			for(int i = 0; i < pdus.length; i++){
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);				
				content.append(msgs[i].getMessageBody());
			}
			
			sender = msgs[0].getOriginatingAddress();
			message = content.toString();
			
			TelephonyManager mTM = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
			datetime = DateFormat.getDateTimeInstance().format(new Date()).replace(" ", "");
			timestamp = mTM.getDeviceId() + datetime;
			
			ContentValues values = new ContentValues();
		    values.put(MessageTable.COLUMN_STATUS, "0");
		    values.put(MessageTable.COLUMN_SENDER, sender);
		    values.put(MessageTable.COLUMN_MESSAGE, message);
		    values.put(MessageTable.COLUMN_TIMESTAMP, timestamp);
		    Log.d("Janja Service:", "Inserting message...");
		    context.getContentResolver().insert(JanjaContentProvider.CONTENT_URI, values);
		    
			if(!isJanjaServiceRunning(context)){
				Log.d("Janja Service:", "Restarting the service...");
				context.startService(smsReplyIntent);
			}
			
		}
		}
	}
	
	private boolean isJanjaServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (JsonRemoteService.class.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
}
