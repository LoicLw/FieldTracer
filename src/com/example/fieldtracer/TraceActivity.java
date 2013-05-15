package com.example.fieldtracer;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.MyLocationOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.GeoPoint;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.os.AsyncTask;

public class TraceActivity extends MapActivity {
	
private TextView editLocation = null;   
private ToggleButton mToggle = null;
private static final String TAG = "Debug";
Date secondDate = new Date();

private Long time_diff;
private File file;
private Location current_loc;
	public Location getCurrent_loc() {
		return current_loc;
	}


private InternetFileCheck Tester = new InternetFileCheck();

// list of markers
private MapView mapView;
private MyLocationOverlay myLocationOverlay;

final Context context = this;
private Boolean trace_toggle = false;
private String trace_name = "";
	public String getTrace_name() {
		return trace_name;
	}


private String boxText ="";
private Vector <GeoPoint> coordinate_vector = new Vector<GeoPoint>();

	private Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(geoPoint, Marker.boundCenterBottom(drawable));
	}
	
	private static Polyline createPolyline(Vector geoPoints) {				
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);		
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.MAGENTA);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(7);
		paintStroke.setPathEffect(new DashPathEffect(new float[] { 25, 15 }, 0));
		return new Polyline(polygonalChain, paintStroke);
	}

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
				Toast.makeText(getApplicationContext(), "Please check that the GPS is enabled",Toast.LENGTH_SHORT).show();
				return;
			}
		}
		
		this.mapView.getOverlays().add(this.myLocationOverlay);
		
		String mMapFileName = SettingsActivity.getMapFile() ;
		if (mMapFileName != "") {
			Toast.makeText(getApplicationContext(), "Map is: " + mMapFileName,Toast.LENGTH_SHORT).show();
		} else {
			Log.v(TAG,"------------------" +"Map path error: "+ mMapFileName + "---------------------");		
		}			
	
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
		    	String lati = null;
				String longi = null;
				String accur = null;
		    	//Getting the time difference each time onLocationChanged is called
		    	time_diff = (new Date()).getTime() - secondDate.getTime();	    	 	
		    	lati = "~" + String.valueOf(loc.getLatitude()).substring(0, 6);
		    	longi = "~" + String.valueOf(loc.getLongitude()).substring(0, 6);
		    	accur = String.valueOf(loc.getAccuracy()).substring(0, String.valueOf(loc.getAccuracy()).indexOf(".")) + "m";
		    	
		    	//editLocation.setText(loc.getLongitude() + "," + loc.getLatitude());
		    	editLocation.setText(lati+ ", " + longi + ", " + accur);
		    	
		    	// Used to take measurement
		    	//writeToSDCard(loc.getLatitude() +";" +loc.getLongitude()+ ";"+ loc.getAccuracy() + ";"+ time_diff+'\n');
		    	
		    	if (trace_toggle == true) {		    		

		    		//Trace drawing
		    		coordinate_vector.add(new GeoPoint(loc.getLatitude(),loc.getLongitude()));		    		
		    		Polyline polyline = createPolyline(coordinate_vector);		    		
		    		ListOverlay listOverlay = new ListOverlay();
		    		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
		    		overlayItems.add(polyline);		   
		    		mapView.getOverlays().add(listOverlay);
		    		
		    		//Trace recording
		    		if (SettingsActivity.getTracesRecordingType()=="Text"){
		    			if (Tester.getStatus() != AsyncTask.Status.RUNNING){
		    				Tester = new InternetFileCheck();
		    				Tester.execute("http://192.168.100.1:4110/rssi.csv", TraceActivity.this);
		    			}
		    		} 
		    		
		    	}		    	
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
	
	//Not used anymore
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
	
	

	public void writeTraceText(Double longitude, Double latitude, Float accuracy, String name, String content_http ) {
		
		String separator = System.getProperty("line.separator");
		String str = "";
		//Get the time to date the Trace
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		//Writing file to SD card
	    	file = new File(Environment.getExternalStorageDirectory() + "/_FieldTracer/", "SignalTrace_"+ name.replaceAll(" ", "_") +"_" + today.format("%Y%m%d") +".trace");
	        try {
	            FileWriter fWriter = new FileWriter(file, true);
	            
	            str=longitude + "," + latitude + "," + accuracy + "," + name + "," + content_http;
	            fWriter.append(str.toString().trim());
	            fWriter.append(separator);
	            fWriter.close();
			} catch (Exception e) {
				Log.e("Error while trying to write text signal trace to SDCard", e.getMessage());
			}		
	}
	
	
	
	public void ButtonOnCenter(View v ) {
		if (current_loc!=null){
			String longitude = "Longitude: " + current_loc.getLongitude();  Log.v(TAG, longitude);  
	    	String latitude = "Latitude: " + current_loc.getLatitude();   Log.v(TAG, latitude);
			GeoPoint point = new GeoPoint(current_loc.getLatitude(), current_loc.getLongitude());
			this.mapView.getMapViewPosition().setCenter(point);
		}	else {
			messageNoLocation();
		}
	}
	
	public void messageNoLocation(){
		 Toast.makeText(this, "Please wait for the GPS to acquire a position", Toast.LENGTH_LONG).show();
	}
	
	
	public void onTraceToggleClicked(View view) {
			 
		//if (current_loc!=null){
		if (true){
		    // Is the toggle on?
		    boolean on = ((ToggleButton) view).isChecked();
		    
		    if (on) {
		    	// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(context);
				final View promptsView = li.inflate(R.layout.trace_prompt, null);
	
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
	
				// set prompts.xml to alertdialog builder
				alertDialogBuilder.setView(promptsView);
	
				final EditText userInput = (EditText) promptsView
						.findViewById(R.id.editTextDialogUserInput);

	 
				// set dialog message
				alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
					    	trace_name = userInput.getText().toString();
					    	trace_toggle=true;
 	
					    }
					  })
					.setNegativeButton("Cancel",
					  new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog,int id) {
					    	//Force un toggle button
					    	mToggle = (ToggleButton)findViewById(R.id.toggleTrace);
					    	mToggle.setChecked(false);
					    	trace_toggle=false;
					    	dialog.cancel();
					    }
					  });	
				// create alert dialog and show it
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();		

		    } else {
		    	trace_toggle=false;
		    	coordinate_vector.clear();
		    	if (trace_name!=""){
		    		//closeTraceGPX(trace_name);
		    	}		    	
		    }		    	
	    }	else {
	    	messageNoLocation();
	    }   
		
	}
	
}
