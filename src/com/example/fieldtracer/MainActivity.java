package com.example.fieldtracer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
		
		editMessage = (TextView) findViewById(R.id.textView1);  
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
	      Toast.makeText(this, "Menu Item trace selected", Toast.LENGTH_SHORT)
	          .show();
	      //Starting the new activity through an intent
	      Intent intent_trace = new Intent(MainActivity.this,TraceActivity.class);
	      MainActivity.this.startActivity(intent_trace);
	      break;
	      
	    case R.id.menu_share:
	      Toast.makeText(this, "Menu item share selected", Toast.LENGTH_SHORT)
	          .show();
	      Intent intent_share = new Intent(MainActivity.this,ShareActivity.class);
	      MainActivity.this.startActivity(intent_share);
	      break;

	    case R.id.menu_settings:
		      Toast.makeText(this, "Menu item settings selected", Toast.LENGTH_SHORT)
		          .show();
		  Intent intent_settings = new Intent(MainActivity.this,SettingsActivity.class);
		  MainActivity.this.startActivity(intent_settings);
		  break;
		      
	    case R.id.menu_tools:
		      Toast.makeText(this, "Menu item tools selected", Toast.LENGTH_SHORT)
		          .show();
		      Intent intent_tools = new Intent(MainActivity.this,ToolsActivity.class);
			  MainActivity.this.startActivity(intent_tools);
		      break;
		      
	    case R.id.menu_home:
		      Toast.makeText(this, "Menu item home selected", Toast.LENGTH_SHORT)
		          .show();
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
	    	  Toast.makeText(this, "Trying to deactivate GPS", Toast.LENGTH_SHORT)
	          .show();  
	    	  locationManager.removeUpdates(locationListener);
	    	  
	    	  //GPs deactivation TODO rework
	    	  /*
	    	  Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
	    	  intent.putExtra("enabled", false);
	    	  sendBroadcast(intent);	    
	    	  */
	    	  editMessage.setText("Before tracing, please make sure the GPS is fixed");
	    	  editMessage.setTextColor(Color.parseColor("#000000"));
	    	  
	        break;
	      }
	}
}
