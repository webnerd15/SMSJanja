package com.webnerdtz.smsjanja;

import java.util.List;

import com.webnerdtz.smsjanja.database.JanjaDbHandler;
import com.webnerdtz.smsjanja.database.JanjaMessage;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

public class JanjaIntentService extends IntentService {

	private JanjaDbHandler mDB;
	
	public JanjaIntentService() {
		super("JanjaIntentService");
		// TODO Auto-ge constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		mDB = JanjaDbHandler.getInstance(getBaseContext());
		List<JanjaMessage> messages = mDB.getAllPendingMessages();
		mDB.close();
		Intent janjaIntent = new Intent(getBaseContext(), JsonRemoteService.class);
		if(!isJanjaServiceRunning(getBaseContext())){
			startService(janjaIntent);
		}else if( isJanjaServiceRunning(getBaseContext()) && !messages.isEmpty()){
			stopService(janjaIntent);
		}else{
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
