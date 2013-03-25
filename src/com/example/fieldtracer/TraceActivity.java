package com.example.fieldtracer;

import java.io.File;
import java.io.FileWriter;
import java.util.Date;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.MyLocationOverlay;
import org.mapsforge.core.model.GeoPoint;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class TraceActivity extends MapActivity {
	
private TextView editLocation = null;   
private static final String TAG = "Debug";
Date secondDate = new Date();
private Long time_diff;
private File file;
private Location current_loc;

// list of markers

private MapView mapView;
private MyLocationOverlay myLocationOverlay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		// Show the Up button in the action bar.
		setupActionBar();
		editLocation = (TextView) findViewById(R.id.textView1);   
		
		//Mapsforge objects
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		
		//Enable MyLocationOverlay, center maps on Location and add small icon
		Drawable drawable = getResources().getDrawable(R.drawable.ic_maps_indicator_current_position_anim1);
		drawable = Marker.boundCenter(drawable);
		this.myLocationOverlay = new MyLocationOverlay(this, this.mapView, drawable);
		
		if (!this.myLocationOverlay.isMyLocationEnabled()) {
			if (!this.myLocationOverlay.enableMyLocation(true)) {
				Toast.makeText(getApplicationContext(), "Erreur de MyLocation",Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		this.mapView.getOverlays().add(this.myLocationOverlay);
		
		//Get the hardcoded map
		String mMapFileName = null;
		mMapFileName = "/adl.map";
		
		String mMapDataPath = Environment.getExternalStorageDirectory().getPath();
		mMapFileName = mMapDataPath + mMapFileName;
		Log.v(TAG,"------------------" +"Map path is : "+ mMapFileName + "---------------------");
		mapView.setMapFile(new File(mMapFileName));
		
		//Add the map to the layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.linearLayout);
		layout.addView(mapView);

		//GPS acquisition part
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		
		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			// Called when a new location is found by the network location provider.
		    public void onLocationChanged(Location loc) {
		    	//Getting the time difference each time onLocationChanged is called
		    	time_diff = (new Date()).getTime() - secondDate.getTime();	    	 		    	
		    	editLocation.setText(loc.getLongitude() + "," + loc.getLatitude());
		    	// Used to take measurement
		    	//writeToSDCard(loc.getLatitude() +";" +loc.getLongitude()+ ";"+ loc.getAccuracy() + ";"+ time_diff+'\n');
		    	secondDate = new Date();
		    	current_loc=loc;
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
			// activity, the Up button is shown.
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void writeToSDCard(String str) {
		String separator = System.getProperty("line.separator");
		if ( str.toString().equals("")) {
			Toast.makeText(this, "You must write 'File Name' and the text, will be written...", Toast.LENGTH_LONG).show();
		}else {// writing file to SD card
	    	file = new File(Environment.getExternalStorageDirectory(), "toto.txt");
	        try {
	            FileWriter fWriter = new FileWriter(file, true);
	            fWriter.append(str.toString().trim());
	            fWriter.append(separator);
	            fWriter.close();
	            Toast.makeText(this, "Your text was written to SD Card succesfully...", Toast.LENGTH_LONG).show();
			} catch (Exception e) {
				Log.e("WRITE TO SD", e.getMessage());
			}
		}		
	}
	
	public void ButtonOnCenter(View v ) {
		// TODO add case location is not available
		String longitude = "Longitude: " + current_loc.getLongitude();  Log.v(TAG, longitude);  
    	String latitude = "Latitude: " + current_loc.getLatitude();   Log.v(TAG, latitude);
		GeoPoint point = new GeoPoint(current_loc.getLatitude(), current_loc.getLongitude());

		//MapFileInfo mapFileInfo = this.mapView.getMapDatabase().getMapFileInfo();
		this.mapView.getMapViewPosition().setCenter(point);
	}

}
