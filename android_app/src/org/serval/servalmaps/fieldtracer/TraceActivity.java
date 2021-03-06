package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.util.Date;
import java.util.List;
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
import org.serval.servalmaps.fieldtracer.utils.DisplayTools;
import org.serval.servalmaps.fieldtracer.utils.FileTools;
import org.serval.servalmaps.fieldtracer.utils.TextDrawer;
import org.serval.servalmaps.fieldtracer.utils.TracesSaving;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class TraceActivity extends MapActivity {

	private TextView editLocation = null;
	private ToggleButton mToggle = null;
	private static final String TAG = "Debug";
	Date secondDate = new Date();

	private Location current_loc;
	final Integer RATE_TEXT_SIZE_PER_SCREEN_SIZE = 12343;

	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;

	final Context context = this;
	private Boolean trace_toggle = false;
	private String poi_name, trace_name, trace_type = "";
	private Vector<GeoPoint> coordinate_vector = new Vector<GeoPoint>();

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
		return new Polyline(polygonalChain, paintStroke);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trace);
		// Show the Up button in the action bar.
		setupActionBar();
		editLocation = (TextView) findViewById(R.id.textView1);

		// Mapsforge objects
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		// Enable MyLocationOverlay, center maps on Location and add small icon
		Drawable drawable = getResources().getDrawable(
				R.drawable.ic_maps_indicator_current_position_anim1);
		drawable = Marker.boundCenter(drawable);
		this.myLocationOverlay = new MyLocationOverlay(this, this.mapView,
				drawable);

		if (!this.myLocationOverlay.isMyLocationEnabled()) {
			if (!this.myLocationOverlay.enableMyLocation(true)) {
				Toast.makeText(getApplicationContext(),
						"Please check that the GPS is enabled",
						Toast.LENGTH_SHORT).show();
				return;
			}
		}

		this.mapView.getOverlays().add(this.myLocationOverlay);

		String mMapFileName = SettingsActivity.getMapFile();
		if (mMapFileName != "") {
			Toast.makeText(getApplicationContext(),
					"Map used: " + mMapFileName, Toast.LENGTH_SHORT).show();
		} else {
			Log.v(TAG, "Map path error" + mMapFileName);
		}

		Log.v(TAG, "Map path is : " + mMapFileName);
		mapView.setMapFile(new File(mMapFileName));

		// Add the map to the layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.linearLayout2);
		layout.addView(mapView);

		// GPS acquisition part
		// Acquire a reference to the system Location Manager
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		LocationListener locationListener = new LocationListener() {
			// Called when a new location is found by the network location
			// provider.
			public void onLocationChanged(Location loc) {
				String lati, longi, accur = null;

				lati = "~" + String.valueOf(loc.getLatitude()).substring(0, 6);
				longi = "~"
						+ String.valueOf(loc.getLongitude()).substring(0, 6);
				accur = String.valueOf(loc.getAccuracy()).substring(0,
						String.valueOf(loc.getAccuracy()).indexOf("."))
						+ "m";
				editLocation.setText(lati + ", " + longi + ", " + accur);

				if (trace_toggle == true) {
					// Trace drawing using a Vector of GeoPoints
					coordinate_vector.add(new GeoPoint(loc.getLatitude(), loc
							.getLongitude()));
					Vector<GeoPoint> sub_coordinate_vector = new Vector<GeoPoint>();

					if (coordinate_vector.size() >= 2) {
						sub_coordinate_vector.add(coordinate_vector
								.get(coordinate_vector.size() - 1));
						sub_coordinate_vector.add(coordinate_vector
								.get(coordinate_vector.size() - 2));

						Polyline polyline = createPolyline(sub_coordinate_vector);
						ListOverlay listOverlay = new ListOverlay();
						List<OverlayItem> overlayItems = listOverlay
								.getOverlayItems();
						overlayItems.add(polyline);
						mapView.getOverlays().add(listOverlay);
					}

					// Every 5 point we save the trace in case the user want to
					// retrieve the last trace
					if ((coordinate_vector.size() % 5) == 0) {
						FileTools.saveTraceObject(coordinate_vector);
					}

					// Trace recording
					if (SettingsActivity.getTracesRecordingType() == "Text") {
						TracesSaving.writeTraceText(loc.getLongitude(),
								loc.getLatitude(), loc.getAccuracy(),
								trace_name);
					} else {
						if (SettingsActivity.getTracesRecordingType() == "GPX") {
							TracesSaving.writeTraceGPX(loc.getLongitude(),
									loc.getLatitude(), loc.getAccuracy(),
									trace_name, trace_type);
						}
					}
				}
				secondDate = new Date();
				current_loc = loc;
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "Gps enabled",
						Toast.LENGTH_SHORT).show();
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "Gps disabled",
						Toast.LENGTH_SHORT).show();
			}
		};

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
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

	public void ButtonOnCenter(View v) {
		if (current_loc != null) {
			String longitude = "Longitude: " + current_loc.getLongitude();
			Log.v(TAG, longitude);
			String latitude = "Latitude: " + current_loc.getLatitude();
			Log.v(TAG, latitude);
			GeoPoint point = new GeoPoint(current_loc.getLatitude(),
					current_loc.getLongitude());
			this.mapView.getMapViewPosition().setCenter(point);
		} else {
			messageNoLocation();
		}
	}

	public void messageNoLocation() {
		Toast.makeText(this, "Please wait for the GPS to acquire a position",
				Toast.LENGTH_LONG).show();
	}

	public void ButtonOnPOIAdd(View v) {
		if (current_loc != null) {
			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(context);
			final View promptsView = li.inflate(R.layout.poi_prompt, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);

			// set prompts.xml to alertdialog builder
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.editTextDialogUserInput);

			final Activity activity = this;
			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// get user input and set it to result
									// edit text
									poi_name = userInput.getText().toString();
									String poi_type = DisplayTools
											.getCheckBoxText(promptsView);

									try {
										TracesSaving.writePOI(
												current_loc.getLongitude(),
												current_loc.getLatitude(),
												current_loc.getAccuracy(),
												poi_name, poi_type, activity);

										Marker marker1 = createMarker(
												R.drawable.marker_green,
												new GeoPoint(current_loc
														.getLatitude(),
														current_loc
																.getLongitude()));
										ListOverlay listOverlay = new ListOverlay();
										List<OverlayItem> overlayItems = listOverlay
												.getOverlayItems();
										overlayItems.add(marker1);
										mapView.getOverlays().add(listOverlay);

										float text_size = (float) (HomeActivity.screen_size / RATE_TEXT_SIZE_PER_SCREEN_SIZE);
										TextDrawer.drawTextOnMap(poi_name,
												current_loc.getLatitude(),
												current_loc.getLongitude(),
												0xcc222200, mapView,
												getCacheDir(), text_size);

									} catch (Exception e) {
										Log.e("Error while trying to write Poi and display an Overlay",
												e.getMessage());
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();

		} else {
			messageNoLocation();
		}
	}

	public void onTraceToggleClicked(View view) {
		if (current_loc != null) {
			// Is the toggle on?
			boolean on = ((ToggleButton) view).isChecked();
			if (on) {
				// get prompts.xml view
				LayoutInflater li = LayoutInflater.from(context);
				final View promptsView = li
						.inflate(R.layout.trace_prompt, null);

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
									public void onClick(DialogInterface dialog,
											int id) {
										trace_name = userInput.getText()
												.toString();
										trace_toggle = true;
										trace_type = DisplayTools
												.getCheckBoxText(promptsView);
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// Force un toggle button
										mToggle = (ToggleButton) findViewById(R.id.toggleTrace);
										mToggle.setChecked(false);
										trace_toggle = false;
										dialog.cancel();
									}
								});
				// create alert dialog and show it
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();

			} else {
				trace_toggle = false;
				coordinate_vector.clear();
				if (trace_name != "") {
					TracesSaving.closeTraceGPX(trace_name, this);
				}
			}
		} else {
			messageNoLocation();
		}

	}

	public boolean onDisplaySavedTrace(MenuItem item) {
		Object object = FileTools.retrieveTraceObject();
		Polyline polyline = createPolyline((Vector) object);
		ListOverlay listOverlay = new ListOverlay();
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
		overlayItems.add(polyline);
		mapView.getOverlays().add(listOverlay);
		return true;
	}
}
