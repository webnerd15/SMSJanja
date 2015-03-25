package com.webnerdtz.smsjanja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.webnerdtz.smsjanja.database.JanjaDbHandler;
import com.webnerdtz.smsjanja.database.JanjaMessage;

public class JsonRemoteService extends Service {

	SharedPreferences prefs;
	String prefName = "JsonRemotePrefs";
	String REMOTE_WEB_URL = "http://smsjanja.com/api/";
	String API_KEY = null;
	boolean REPLY_ONLINE_MESSAGES = true;
	private int RECURSIVE_TIMER_SECONDS = 10;
	private static final String TAG_PHONE_NUMBER = "phone";
	private static final String TAG_MESSAGE = "message";
	private static final String TAG_STATUS = "status";
	
	private int SIMPLE_NOTFICATION_ID = 1010;
	
	private Timer mTimer = null;
	private Handler mHandler = new Handler();
	private JanjaDbHandler mDB;
	
	@Override
    public void onCreate() {
          super.onCreate();
          SharedPreferences prefs = getSharedPreferences(prefName, MODE_PRIVATE);
          REMOTE_WEB_URL = prefs.getString("REMOTE_WEB_URL", "");
          API_KEY = prefs.getString("API_KEY", "");
          REPLY_ONLINE_MESSAGES = prefs.getBoolean("REPLY_ONLINE_MESSAGES", true);
          RECURSIVE_TIMER_SECONDS = prefs.getInt("RECURSIVE_TIMER_SECONDS", 10);
          
          mDB = JanjaDbHandler.getInstance(getBaseContext());
                    
          updateNotification("Running...");
          
          Intent intent = new Intent(this, JanjaIntentService.class);
          PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
          AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
          am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 20*1000, pi);
    }
    
	@Override
	public void onStart(Intent intent, int startId){

		updateNotification("Ready...");
				
        if(mTimer != null){
        	mTimer.cancel();
        }else{
        	mTimer = new Timer();
        }
        // schedule the the timer task
        mTimer.scheduleAtFixedRate(new JanjaTask(), 0, 1000*RECURSIVE_TIMER_SECONDS);
        
	}
	
    @Override
    public void onDestroy() {
		super.onDestroy();
    }
    
    @Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
    
    protected void sendJson(final String fromNumber, final String fromMessage, final String timeStamp, final int messageID) {
        Thread t = new Thread() {

            public void run() {
                
            	HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response = null;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(REMOTE_WEB_URL);
                    json.put("fromNumber", fromNumber);
                    json.put("fromMessage", fromMessage);
                    json.put("timestamp", timeStamp);
                    json.put("api_key", API_KEY);
                    
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                    HttpConnectionParams.setSoTimeout(httpParams, 10000);
                    
                    post.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
                    post.setHeader("json", json.toString());
                    
                    if(!REMOTE_WEB_URL.isEmpty()){
                    	response = client.execute(post);
                    }
                    /*Checking response */
                    if(response!=null){
                    	String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
         			    JSONObject object = null;
         			    if(!jsonResult.isEmpty()){
         			    	object = new JSONObject(jsonResult);
         		 
	         		   		String phoneNumber = object.getString(TAG_PHONE_NUMBER);
	         		   		String message = object.getString(TAG_MESSAGE);
	         		   		String status = object.getString(TAG_STATUS);
	         		   		
	         		   		if(messageID > 0){
	         		   			JanjaMessage janja_message = new JanjaMessage();
		         		   		janja_message.setID(messageID);
		         		   		mDB.deleteMessage(janja_message); 
	         		   		}
	         		   		if(!status.equals("duplicate") || status.equals("done")){
	         		   			sendSMS(phoneNumber, message);
	         		   		}
         			    }
                    }else{
                    	updateNotification("Can't connect to server");                
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("Janja Service:", "Cannot Establish Connection");
                } catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
                //Looper.loop(); //Loop in the message queue
            }
        };

        t.start();      
    }

    private StringBuilder inputStreamToString(InputStream is) {
        String rLine = "";
        StringBuilder answer = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
          
        try {
         while ((rLine = rd.readLine()) != null) {
          answer.append(rLine);
           }
        }
          
        catch (IOException e) {
            e.printStackTrace();
         }
        return answer;
    }
    private void sendSMS(final String phoneNumber, final String message){
    	String SENT = "SMS_SENT";
    	String DELIVERED = "SMS_DELIVERED";
    	
    	PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
    	PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
    	
    	// when sms has been sent...
    	registerReceiver( new BroadcastReceiver(){
			@Override
			public void onReceive(Context arg0, Intent arg1) {

				switch (getResultCode())
    			{
    				case Activity.RESULT_OK:
    			        updateNotification("Working for: " + phoneNumber);  			        
    					break;
    				case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
    					updateNotification("Generic error for: " + phoneNumber);
    					break;
    				case SmsManager.RESULT_ERROR_NO_SERVICE:
    					
    					break;
    				case SmsManager.RESULT_ERROR_NULL_PDU:
    					Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
    					break;
    				case SmsManager.RESULT_ERROR_RADIO_OFF:
    					Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
    					break;
    			}
			}
    	}, new IntentFilter(SENT));
    	
    	// when sms has been delivered...
    	registerReceiver( new BroadcastReceiver(){
    		@Override
    		public void onReceive(Context arg0, Intent arg1){
		        
    			switch (getResultCode())
    			{
    				case Activity.RESULT_OK:
    					updateNotification("Replied to: " + phoneNumber);
    					break;
    				case Activity.RESULT_CANCELED:
    					updateNotification("Not Replied to: " + phoneNumber);
    					break;
    			}
    		}
    	}, new IntentFilter(DELIVERED));
    	
    	SmsManager sms = SmsManager.getDefault();
    	if(phoneNumber.length() >= 10){
	    	if(PhoneNumberUtils.isWellFormedSmsAddress(phoneNumber)){
	    		sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
	    	}
    	
	    	ContentValues values = new ContentValues();    		              
	        values.put("address", phoneNumber);
	        values.put("body", message );
	        getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    	}
    }


    private class JanjaTask extends TimerTask{

		@Override
		public void run() {
			
			mHandler.post(new Runnable(){

				@Override
				public void run() {
					
			        List<JanjaMessage> messages = mDB.getAllPendingMessages();       
			         
			        if(!messages.isEmpty()){
				        for (JanjaMessage msg : messages) {
				        	sendJson(msg.getSender(), msg.getMessage(), msg.getTimestamp(), msg.getID());
				        }
			        }else{			        
				        if(REPLY_ONLINE_MESSAGES){
				        	Log.d("Janja service:", "Cheking for online messages using " + API_KEY);
				        	sendJson("replicator", "checking for pending message", "", 0);
				        }
			        }
				}
		        
			});
		}
    	
    }
    
    @SuppressWarnings("deprecation")
	private void updateNotification(String message){
    	//Get the Notification Service
        NotificationManager notifier = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        
        //Get the icon for the notification
        Notification notification = new Notification(R.drawable.icon, "SMS Janja!", System.currentTimeMillis());
        
        //Setup the Intent to open this Activity when clicked
        Intent toLaunch = new Intent(getBaseContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), 0, toLaunch, 0);
        
        //Set the Notification Info
        notification.setLatestEventInfo(this, "SMS Janja!", message, contentIntent);
        
        //Setting Notification Flags
        notification.flags |= Notification.FLAG_NO_CLEAR;
        
        //Send the notification
        notifier.notify(SIMPLE_NOTFICATION_ID, notification);
    }

}
