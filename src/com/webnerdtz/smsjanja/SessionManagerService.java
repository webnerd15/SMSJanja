package com.webnerdtz.smsjanja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class SessionManagerService extends Service {

	SharedPreferences prefs;
	String prefName = "JsonRemotePrefs";
	String REMOTE_WEB_URL=null;
	String API_USERNAME=null;
	String API_USERPASSWORD=null;
	private static final String TAG_STATUS = "status";
	private static final String TAG_API_KEY = "api_key";
		
	@Override
    public void onCreate() {
          super.onCreate();
          //Toast.makeText(this,"SMS Janja started ...", Toast.LENGTH_LONG).show();
          SharedPreferences prefs = getSharedPreferences(prefName, MODE_PRIVATE);
          REMOTE_WEB_URL = prefs.getString("REMOTE_WEB_URL", "");
          API_USERNAME = prefs.getString("API_USERNAME", "");
          API_USERPASSWORD = prefs.getString("API_USERPASSWORD", "");
    }
    
	@Override
	public void onStart(Intent intent, int startId){
        authenticateUser(API_USERNAME, API_USERPASSWORD);
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
    
    public void authenticateUser(final String username, final String password) {
        Thread t = new Thread() {

            public void run() {
               // Looper.prepare(); //For Preparing Message Pool for the child Thread
                
            	HttpClient client = new DefaultHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                HttpResponse response = null;
                JSONObject json = new JSONObject();

                try {
                    HttpPost post = new HttpPost(REMOTE_WEB_URL);
                    json.put("username", username);
                    json.put("password", password);
                    
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
                    	Log.d("Janja Service:", "API Response recived..");
                    	String jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
         			    JSONObject object = null;
         			    if(!jsonResult.isEmpty()){
         			    	object = new JSONObject(jsonResult);
         		 
	         		   		String status = object.getString(TAG_STATUS);
	         		   		String api_key = object.getString(TAG_API_KEY);
	         		   		if(status.equals("OK")){
		         		   		prefs = getSharedPreferences(prefName, MODE_MULTI_PROCESS);
		    					SharedPreferences.Editor editor = prefs.edit();
		    					editor.remove("API_KEY");
		    					editor.apply();
		    					editor.putString("API_KEY", api_key);		    					
		    					editor.apply();
		    					
		    					Intent mainActivityIntent = new Intent(getBaseContext(), MainActivity.class);
		    					mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		    					startActivity(mainActivityIntent);
	         		   		}else{
	         		   			Log.d("Janja Service:", "API USER IS NOT VALID!");
	         		   		}	         		   		
         			    }
                    }else{
                    	Log.d("Janja Service:", "Could not get reply from API server");
                    }

                } catch(JSONException e) {
                    e.printStackTrace();
                    Log.d("Janja Service:", "Cannot Understand the result");
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

}
