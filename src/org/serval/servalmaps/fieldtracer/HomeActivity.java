package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.serval.servalmaps.fieldtracer.utils.FileTools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private TextView editMessage = null;
	private LocationManager locationManager;
	private LocationListener locationListener;
	public static Integer screen_size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		//If the user already specified a map we set it
		SharedPreferences settings = getSharedPreferences("UserInfo", 0);
		if (settings.getString("CurrentMap", "").toString() != ""){
			SettingsActivity.setMapFile(settings.getString("CurrentMap", "").toString()) ;
		}

		// Create the directories at start and copy Adelaide mapfile from assets
		// folder
		FileTools.createDirIfNotExists(SettingsActivity.APP_NAME_PATH);
		FileTools.createDirIfNotExists(SettingsActivity.APP_NAME_PATH + "temp");
		File file = new File(Environment.getExternalStorageDirectory()
				.getPath()
				+ SettingsActivity.APP_NAME_PATH
				+ "Adelaide_Flinders#_-35.03,138.6#_-34.99,138.56.map");

		if (!file.exists()) {
			FileTools.copyAssets(this);
		}
		editMessage = (TextView) findViewById(R.id.textView2);
		screen_size = getScreenPixels();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_trace:

			Intent intent_trace = new Intent(HomeActivity.this,
					TraceActivity.class);
			HomeActivity.this.startActivity(intent_trace);
			break;

		case R.id.menu_share:

			Intent intent_share = new Intent(HomeActivity.this,
					ShareActivity.class);
			HomeActivity.this.startActivity(intent_share);
			break;

		case R.id.menu_settings:

			Intent intent_settings = new Intent(HomeActivity.this,
					SettingsActivity.class);
			HomeActivity.this.startActivity(intent_settings);
			break;

		case R.id.menu_tools:

			Intent intent_tools = new Intent(HomeActivity.this,
					ToolsActivity.class);
			HomeActivity.this.startActivity(intent_tools);
			break;

		default:
			break;
		}

		return true;
	}

	// This function activate the GPS sensor throughout the application usage.
	// It enable people that uses devices with long acquisition time to prepare
	// their device.
	public void GPSOnClick(View v) {
		switch (v.getId()) {
		case R.id.bOn:
			Toast.makeText(this, "Trying to activate GPS", Toast.LENGTH_SHORT)
					.show();

			// Acquire a reference to the system Location Manager
			locationManager = (LocationManager) this
					.getSystemService(Context.LOCATION_SERVICE);
			// Define a listener that responds to location updates
			locationListener = new LocationListener() {
				public void onLocationChanged(Location loc) {
					// Called when a new location is found by the network
					// location provider.
					editMessage.setText("Location acquired.");
					editMessage.setTextColor(Color.parseColor("#10FF10"));
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {
				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}
			};

			// Register the listener with the Location Manager to receive
			// location updates
			locationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			break;
		case R.id.bOff:
			editMessage
					.setText("Before tracing, please make sure the GPS is fixed");
			editMessage.setTextColor(Color.parseColor("#000000"));

			System.runFinalizersOnExit(true);
			System.exit(0);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	public Integer getScreenPixels() {

		int Measuredwidth = 0;
		int Measuredheight = 0;
		Point size = new Point();
		WindowManager w = getWindowManager();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			w.getDefaultDisplay().getSize(size);

			Measuredwidth = size.x;
			Measuredheight = size.y;
		} else {
			Display d = w.getDefaultDisplay();
			Measuredwidth = d.getWidth();
			Measuredheight = d.getHeight();
		}

		return Measuredwidth * Measuredheight;
	}
}
