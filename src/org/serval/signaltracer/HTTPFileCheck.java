
package org.serval.signaltracer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.util.Log;


public class HTTPFileCheck extends AsyncTask<Object, Void, Boolean> {

private String content = "";
private String total = ""; 	
private TraceActivity activity_trace;
	
	  public String getTotal() {
		  return total;
	  }

	public String getContenu() {
		  return content;
	  }

	@Override
	  protected Boolean doInBackground(Object... params) {
		Log.v("ASYNC",params[1].getClass().getName());
		activity_trace= (TraceActivity) params[1];
	   Boolean sonuc = null;
	   try {
	    URL u = new URL((String) params[0]);
	    HttpURLConnection huc = (HttpURLConnection) u.openConnection();
	    huc.setRequestMethod("GET");
	    huc.connect();
	    int code = huc.getResponseCode();

	    if (code == HttpURLConnection.HTTP_OK) {
	     sonuc = true;
	     BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
	     
	    while((content=reader.readLine()) != null){
	    	Log.v("ASYNC", "Content was: " + content);
	    	total = total + content;
	    }
	     
	    } else {
	     sonuc = false;
	     Log.v("ASYNC", "HTTP request did not work");
	     
	    }
	   } catch (Exception e) {
		   Log.v("ASYNC", "Exception caught" + '\n' + e);
	    sonuc = false;
	   }
	   return sonuc;
	  }

	  @Override
	  protected void onPostExecute(Boolean result) {	  
		  activity_trace.writeTraceText(activity_trace.getCurrent_loc().getLongitude(), activity_trace.getCurrent_loc().getLatitude(), activity_trace.getCurrent_loc().getAccuracy(), activity_trace.getTrace_name(), total);
	  }
	 }
