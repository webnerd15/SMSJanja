package com.webnerdtz.smsjanja;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {
	
	SharedPreferences prefs;
	String prefName = "JsonRemotePrefs";
	String REMOTE_WEB_URL = "http://smsjanja.com/api/";	
	String API_KEY = null;
	WebView janjaWebView;
	
	JanjaAlarmReceiver alarm = new JanjaAlarmReceiver();
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// prepare the web api url
		prefs = getSharedPreferences(prefName, MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("REMOTE_WEB_URL", REMOTE_WEB_URL);
		editor.apply();
		API_KEY = prefs.getString("API_KEY", "");
		janjaWebView = (WebView) findViewById(R.id.janjaWebView);
		janjaWebView.setWebViewClient(new JanjaWebviewClient());
		WebSettings webSettings = janjaWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		janjaWebView.loadUrl("http://smsjanja.com/api-client/?api_key="+API_KEY);
		
		Intent janjaIntent = new Intent(getBaseContext(), JsonRemoteService.class);
		if(!isJanjaServiceRunning(getBaseContext())){
			startService(janjaIntent);
		}
		
		alarm.setAlarm(this);
		
		if(API_KEY.isEmpty()){
			Intent sessionLoginActivityIntent = new Intent(getBaseContext(), SessionManager.class);
			startActivity(sessionLoginActivityIntent);
			finish();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		super.onCreateOptionsMenu(menu);
		CreateMainMenu(menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		return menuChoice(item);
	}
	
	private void CreateMainMenu(Menu menu){
		menu.setQwertyMode(true);
		MenuItem newSmsMenu = menu.add(0,0,0,"Create New");
		{
			newSmsMenu.setAlphabeticShortcut('n');
			newSmsMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			        smsIntent.setType("vnd.android-dir/mms-sms");
			        startActivity(smsIntent);
					return false;
				}
			});
		}
		MenuItem viewAllMenu = menu.add(0,0,0,"View All");
		{
			viewAllMenu.setAlphabeticShortcut('v');
			viewAllMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					Intent allSmsIntent = new Intent(Intent.ACTION_MAIN);
				    allSmsIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				    allSmsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    allSmsIntent.setClassName("com.android.mms", "com.android.mms.ui.ConversationList");
					startActivity(allSmsIntent);
					return false;
				}
			});
		}
		MenuItem settingsMenu = menu.add(0,0,0,"Settings");
		{
			settingsMenu.setAlphabeticShortcut('s');
			settingsMenu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				
				@Override
				public boolean onMenuItemClick(MenuItem arg0) {
					Intent settingsActivityIntent = new Intent(getBaseContext(), JsonRemoteSettings.class);
					startActivity(settingsActivityIntent);
					return false;
				}
			});
		}
	}
	
	private boolean menuChoice(MenuItem item){
		switch(item.getItemId()){
			case 0:
				return true;
			case 1:
				return true;
			case 2:
				return true;
		}
		return false;
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
	
	private class JanjaWebviewClient extends WebViewClient {
	 	@Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	        if (Uri.parse(url).getHost().equals("smsjanja.com")) {
	            // This is my web site, so do not override; let my WebView load the page
	            return false;
	        }
	        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
	        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	        startActivity(intent);
	        return true;
	    }

}
}