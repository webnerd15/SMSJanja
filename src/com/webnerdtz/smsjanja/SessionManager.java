package com.webnerdtz.smsjanja;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SessionManager extends Activity {
	SharedPreferences prefs;
	String prefName = "JsonRemotePrefs";
	Button btnSessionLogin;
	EditText txtUsername;
	EditText txtPassword;
	TextView statusLabel;
	ProgressBar progressBar;
	
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.session_activity);
	        
	        txtUsername = (EditText)findViewById(R.id.txtEmailAddress);
	        txtPassword = (EditText)findViewById(R.id.txtPassword);
	        
	        statusLabel = (TextView)findViewById(R.id.statusLabel);
	        progressBar = (ProgressBar)findViewById(R.id.progressBar);
	        progressBar.setVisibility(View.GONE);
	        statusLabel.setText("Enter your Username and Password");
	        
	        btnSessionLogin = (Button) findViewById(R.id.btnSessionLogin);
	        btnSessionLogin.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// get the SharedPreferences object
					prefs = getSharedPreferences(prefName, MODE_MULTI_PROCESS);
					SharedPreferences.Editor editor = prefs.edit();
					
					String username = txtUsername.getText().toString();
					String password = txtPassword.getText().toString();
					
					txtUsername.setVisibility(View.INVISIBLE);
					txtPassword.setVisibility(View.INVISIBLE);					
					btnSessionLogin.setVisibility(View.INVISIBLE);
					
					progressBar.setVisibility(View.VISIBLE);
					statusLabel.setText("Please Wait a bit...");
					
					editor.remove("API_USERNAME");
					editor.remove("API_USERPASSWORD");
					editor.apply();
					
					editor.putString("API_USERNAME", username);
					editor.putString("API_USERPASSWORD", password);					
					editor.apply();
					
					Intent sessionIntent = new Intent(getBaseContext(), SessionManagerService.class);
					startService(sessionIntent);
				}
				
			});
	        
	 }
}
