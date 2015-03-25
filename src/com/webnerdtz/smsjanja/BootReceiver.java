package com.webnerdtz.smsjanja;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	JanjaAlarmReceiver alarm = new JanjaAlarmReceiver();
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase("android.intent.action.BOOT_COMPLETED")) {
			Intent janjaIntent = new Intent(context, JsonRemoteService.class);
			context.startService(janjaIntent);
			Log.d("Janja Service:", "Started on system boot...");
			alarm.setAlarm(context);
		}
	}
}
