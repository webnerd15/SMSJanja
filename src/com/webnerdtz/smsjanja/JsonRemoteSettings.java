package com.webnerdtz.smsjanja;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class JsonRemoteSettings extends Activity {
	SharedPreferences prefs;
	String prefName = "JsonRemotePrefs";
	Button btnSavePrefs;
	CheckBox chkReplyMessagesOnline;
	SeekBar timerSeekBar;
	TextView seekBarText;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.settings);
	        
	        chkReplyMessagesOnline = (CheckBox) findViewById(R.id.chkReplyMessagesOnline);
	        timerSeekBar = (SeekBar) findViewById(R.id.timerSeekBar);
	        seekBarText = (TextView) findViewById(R.id.seekBarText);
	        
	        timerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				int stepSize = 10;
				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,
						boolean fromUser) {
					progress = ((int)Math.round(progress/stepSize))*stepSize;
				    
				    if(progress == 0){
				    	progress = 10;
				    }
				    
				    seekBar.setProgress(progress);
				    seekBarText.setText("Process SMS Every: " + timerSeekBar.getProgress() + "Secs");
				}
			});
	        
	        btnSavePrefs = (Button) findViewById(R.id.btnSavePrefs);
	        btnSavePrefs.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// get the SharedPreferences object
					prefs = getSharedPreferences(prefName, MODE_MULTI_PROCESS);
					SharedPreferences.Editor editor = prefs.edit();
					
					// save the preferences
					if(chkReplyMessagesOnline.isChecked()){
						editor.putBoolean("REPLY_ONLINE_MESSAGES", true);
					}else{
						editor.putBoolean("REPLY_ONLINE_MESSAGES", false);
					}
					
					editor.putInt("RECURSIVE_TIMER_SECONDS", timerSeekBar.getProgress());
					editor.apply();
					
					Toast.makeText(getBaseContext(), "Settings saved successfully!", Toast.LENGTH_LONG).show();
					Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
					startActivity(mainActivityIntent);
				}
				
			});
	        
	        // load the preferences...
	        SharedPreferences prefs = getSharedPreferences(prefName, MODE_PRIVATE);
	        timerSeekBar.setProgress(prefs.getInt("RECURSIVE_TIMER_SECONDS", 10));
	        seekBarText.setText("Process SMS Every: " + timerSeekBar.getProgress() + "Secs");
	        boolean replyOnlineMessages = prefs.getBoolean("REPLY_ONLINE_MESSAGES", true);
	        
	        if(replyOnlineMessages){
	        	chkReplyMessagesOnline.setChecked(true);
	        }else{
	        	chkReplyMessagesOnline.setChecked(false);
	        }
	        
	 }
}
