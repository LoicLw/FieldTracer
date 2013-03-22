package com.example.fieldtracer;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

//Other
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Date;

//GPS
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;


public class TraceActivity extends Activity {
	
private TextView editLocation = null;   
private static final String TAG = "Debug";
Date secondDate = new Date();
private Long time_diff;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		// Show the Up button in the action bar.
		setupActionBar();
		
		editLocation = (TextView) findViewById(R.id.textView1);   

	//GPS acquisition part
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location loc) {
		      // Called when a new location is found by the network location provider.
		    	
		    	//Getting the time difference each time onLocationChanged is called
		    	time_diff = (new Date()).getTime() - secondDate.getTime();
		    	   
		    	Toast.makeText(getBaseContext(),
		    	     "Location changed: Lat: " +loc.getLatitude()+" Lng: "  
		    	     + loc.getLongitude(), Toast.LENGTH_SHORT).show();  
		    	String longitude = "Longitude: " + loc.getLongitude();  
		    	Log.v(TAG, longitude);  
		    	String latitude = "Latitude: " + loc.getLatitude();  
		    	Log.v(TAG, latitude);  
		    	editLocation.setText("Lat: " +loc.getLatitude()+" Lng: "  
				     + loc.getLongitude() + " Accuracy : "+ loc.getAccuracy() + " DiffTime : "+ time_diff);
		    	 
		    	secondDate = new Date();
		    }

		    public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {
		    	Toast.makeText(getApplicationContext(), "Gps enabled",Toast.LENGTH_SHORT).show();
		    }

		    public void onProviderDisabled(String provider) {
		    	Toast.makeText(getApplicationContext(), "Gps disabled",Toast.LENGTH_SHORT).show();
		    }		
		  };

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trace, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
