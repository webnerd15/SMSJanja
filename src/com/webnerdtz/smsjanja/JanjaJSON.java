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



public class JanjaJSON {
	
	public static  String getJSONData(String url, String fromNumber, String fromMessage) {

		String jsonResult = null;

	    HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
        HttpResponse response;
        JSONObject json = new JSONObject();

        try {
            HttpPost post = new HttpPost(url);
            json.put("fromNumber", fromNumber);
            json.put("fromMessage", fromMessage);
            
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
            HttpConnectionParams.setSoTimeout(httpParams, 10000);
            
            post.setEntity(new ByteArrayEntity(json.toString().getBytes("UTF8")));
            post.setHeader("json", json.toString());
            
            response = client.execute(post);
            
            /*Checking response */
            if(response!=null){
            	jsonResult = inputStreamToString(response.getEntity().getContent()).toString();
 			    //object = new JSONObject(jsonResult); 		   		
            }else{
            	jsonResult = null;
            }

        } catch(JSONException e) {
            e.printStackTrace();
            //createDialog("Error", "Cannot Establish Connection");
        } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    return jsonResult;
	}
	
	private static StringBuilder inputStreamToString(InputStream is) {
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
