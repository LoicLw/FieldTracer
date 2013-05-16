package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	  
private TextView editMessage = null;   
private LocationManager locationManager;
private LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//Copy a base map for Adelaide
		createDirIfNotExists("_FieldTracer");
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/_FieldTracer/adelaide.map");
		
		if(!file.exists()){
			copyAssets();
		}
		editMessage = (TextView) findViewById(R.id.textView2);  
	}
	
 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.menu_trace:

	      //Starting the new activity through an intent
	      Intent intent_trace = new Intent(MainActivity.this,TraceActivity.class);
	      MainActivity.this.startActivity(intent_trace);
	      break;
	      
	    case R.id.menu_share:

	      Intent intent_share = new Intent(MainActivity.this,ShareActivity.class);
	      MainActivity.this.startActivity(intent_share);
	      break;

	    case R.id.menu_settings:
	
		  Intent intent_settings = new Intent(MainActivity.this,SettingsActivity.class);
		  MainActivity.this.startActivity(intent_settings);
		  break;
		         
	    default:
	      break;
	    }

	    return true;
	  } 

	public void GPSOnClick(View v) {
	    switch (v.getId()) {
	      case R.id.bOn:
	    	  Toast.makeText(this, "Trying to activate GPS", Toast.LENGTH_SHORT)
	          .show();  
	    	  
	    	// Acquire a reference to the system Location Manager
	  		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	  		// Define a listener that responds to location updates
			locationListener = new LocationListener() {
			    public void onLocationChanged(Location loc) {
			      // Called when a new location is found by the network location provider.
			    	editMessage.setText("Location acquired.");
			    	editMessage.setTextColor(Color.parseColor("#10FF10"));
			    }
			    public void onStatusChanged(String provider, int status, Bundle extras) {
			    }
			    public void onProviderEnabled(String provider) {			    }
			    public void onProviderDisabled(String provider) {
			    }		
			  };
	  		
	  	// Register the listener with the Location Manager to receive location updates
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	        break;
	      case R.id.bOff:
	    	  editMessage.setText("Before tracing, please make sure the GPS is fixed");
	    	  editMessage.setTextColor(Color.parseColor("#000000"));
	    	  
	    	  System.runFinalizersOnExit(true);
	    	  System.exit(0); 	    	  
	        break;
	      }
	}
	
	public static boolean createDirIfNotExists(String path) {
	    boolean ret = true;

	    File file = new File(Environment.getExternalStorageDirectory(), path);
	    if (!file.exists()) {
	        if (!file.mkdirs()) {
	            Log.e("Log :: ", "Problem creating folder");
	            ret = false;
	        }
	    }
	    return ret;
	}
	
	private void copyAssets() {
	    AssetManager assetManager = getAssets();
	    String[] files = null;
	    try {
	        files = assetManager.list("");
	    } catch (IOException e) {
	        Log.e("tag", "Failed to get asset file list.", e);
	    }
	    for(String filename : files) {
	        InputStream in = null;
	        OutputStream out = null;
	        try {
	          in = assetManager.open(filename);
	          out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/_FieldTracer/" + filename);
	          copyFile(in, out);
	          in.close();
	          in = null;
	          out.flush();
	          out.close();
	          out = null;
	        } catch(IOException e) {
	            Log.e("tag", "Failed to copy asset file: " + filename, e);
	        }       
	    }
	}
	private void copyFile(InputStream in, OutputStream out) throws IOException {
	    byte[] buffer = new byte[1024];
	    int read;
	    while((read = in.read(buffer)) != -1){
	      out.write(buffer, 0, read);
	    }
	}
}
