package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
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
import org.serval.servalmaps.fieldtracer.utils.BackgroundMaps;
import org.serval.servalmaps.fieldtracer.utils.MapFromString;
import org.serval.servalmaps.fieldtracer.utils.TextDrawer;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

public class VisualizeMapsActivity extends MapActivity {

	private Double map_upperleftcorner_lat;
	private Double map_upperleftcorner_long;
	private Double map_lowerrightcorner_lat;
	private Double map_lowerrightcorner_long;
	private final Integer RATIO_TEXT_SIZE_PER_SCREEN_SIZE = 12343;

	// Maps
	private static Vector<MapFromString> mapsDB = null;

	private Vector<Vector> maps_boundary_box = new Vector<Vector>();
	private Vector<GeoPoint> maps_upper_left = new Vector<GeoPoint>();
	private Vector<GeoPoint> maps_lower_right = new Vector<GeoPoint>();

	private Vector<String> maps_name = new Vector<String>();
	private Vector<String> maps_type = new Vector<String>();

	// list of markers
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;

	private static final String TAG = "CompareMaps Activity";

	public static Vector<MapFromString> getMapsDB() {
		if (mapsDB == null) {
			mapsDB = new Vector<MapFromString>();
		}
		return mapsDB;
	}

	private static Polyline createPolyline(Vector geoPoints, int col) {
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(col);
		paintStroke.setAlpha(200);
		paintStroke.setStrokeWidth(6);
		return new Polyline(polygonalChain, paintStroke);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visualizemaps);
		// Show the Up button in the action bar.
		setupActionBar();

		// Mapsforge objects
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.getMapViewPosition().setCenter(new GeoPoint(0, 0));
		mapView.getMapViewPosition().setZoomLevel((byte) 3);

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

		// world.map is copied from assets in the Main Activity
		File world = new File(Environment.getExternalStorageDirectory()
				.getPath() + SettingsActivity.APP_NAME_PATH + "world.map");

		String mMapFileName = world.getPath();

		Log.v(TAG, "Map path is : " + mMapFileName);
		mapView.setMapFile(new File(mMapFileName));
		// Add the map to the layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.relativeLayoutMaps);
		layout.addView(mapView);

		addMapsFromServalUsers();
		addMapsFromLocalStorage();
		if (mapsDB == null) {
			mapsDB = new Vector<MapFromString>();
		}

		/*
		 * Text format for Mapsforge maps file: Port Augusta and
		 * Pirie#_-31.877558,136.931877#_-33.60547,138.546867.map
		 */

		for (int i = 0; i < mapsDB.size(); i++) {
			MapFromString map_to_draw = mapsDB.get(i);

			map_upperleftcorner_lat = map_to_draw.getMaps_boundary_box()
					.getUpper_left().latitude;
			map_upperleftcorner_long = map_to_draw.getMaps_boundary_box()
					.getUpper_left().longitude;
			map_lowerrightcorner_lat = map_to_draw.getMaps_boundary_box()
					.getLower_right().latitude;
			map_lowerrightcorner_long = map_to_draw.getMaps_boundary_box()
					.getLower_right().longitude;
			String map_name = map_to_draw.getMaps_name();

			// Drawing a rectangle using map boundary box
			Vector<GeoPoint> coordinate_vector = new Vector<GeoPoint>();
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,
					map_upperleftcorner_long));
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,
					map_lowerrightcorner_long));
			coordinate_vector.add(new GeoPoint(map_lowerrightcorner_lat,
					map_lowerrightcorner_long));
			coordinate_vector.add(new GeoPoint(map_lowerrightcorner_lat,
					map_upperleftcorner_long));
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,
					map_upperleftcorner_long));

			Polyline polyline;
			Integer col = 0;
			if (mapsDB.get(i).getMaps_type() == "Users") {
				col = 0xbb0000ff;
				polyline = createPolyline(coordinate_vector, col);
			} else {
				col = Color.BLACK;
				polyline = createPolyline(coordinate_vector, col);
			}

			float text_size = (float) (MainActivity.screen_size / RATIO_TEXT_SIZE_PER_SCREEN_SIZE);
			TextDrawer.drawTextOnMap(map_name,
					map_lowerrightcorner_lat + 0.015,
					(map_upperleftcorner_long + map_lowerrightcorner_long) / 2,
					col, mapView, getCacheDir(), text_size);

			ListOverlay listOverlay = new ListOverlay();
			List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
			overlayItems.add(polyline);
			mapView.getOverlays().add(listOverlay);

		}
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
		getMenuInflater().inflate(R.menu.visualizemaps, menu);
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

	public void addMapsFromServalUsers() {
		BackgroundMaps bg_maps = new BackgroundMaps();
		bg_maps.refreshType(".map", this.getContentResolver());

		// Vector used to store users maps information
		Vector<String> users_maps_name = new Vector<String>();
		Vector<Uri> users_maps_uri = new Vector<Uri>();
		Vector<Integer> users_maps_size = new Vector<Integer>();

		users_maps_name = bg_maps.getMaps_name_from_users();
		users_maps_uri = bg_maps.getMaps_uri_from_users();
		users_maps_size = bg_maps.getMaps_size_from_users();

		List<String> maps_file = new ArrayList<String>();

		for (int i = 0; i < users_maps_name.size(); i++) {
			String s = (String) users_maps_name.elementAt(i);
			Uri uri = (Uri) users_maps_uri.elementAt(i);
			Integer size = (Integer) users_maps_size.elementAt(i);
			Log.v("BackgroundMaps", "One map files was: " + s);
			Log.v("BackgroundMaps", "One file URI was: " + uri.toString());
			Log.v("BackgroundMaps", "One file size was: " + size);
			// TODO check for more than just size
			if (size > 0) {
				Log.v("BackgroundMaps", "This map sounds good " + s);
				maps_file.add(s);
				try {
					s = s.replace(".map", "");
					if (!VisualizeMapsActivity.getMapsDB().contains(
							new MapFromString(s, "Users"))) {
						VisualizeMapsActivity.getMapsDB().add(
								new MapFromString(s, "Users"));
						Log.v(TAG, "------------------It is a new user map: "
								+ s + "--------------------");
					}
				} catch (Exception e) {
					Log.e("BackgroundMaps", "Maps string incorrect " + s);
				}
			} else {
				Log.v("BackgroundMaps", "Map was empty " + s);
				s = "EMPTY MAP_" + s;
			}

		}
	}

	public void addMapsFromLocalStorage() {
		File mPath = new File(Environment.getExternalStorageDirectory(),
				SettingsActivity.APP_NAME_PATH);
		final String FTYPE = ".map";
		String[] mFileList;

		if (mPath.exists()) {
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					File sel = new File(dir, filename);
					return filename.contains(FTYPE) || sel.isDirectory();
				}
			};
			mFileList = mPath.list(filter);
		} else {
			mFileList = new String[0];
		}

		for (int i = 0; i < mFileList.length; i++) {
			try {
				mFileList[i] = mFileList[i].replace(".map", "");
				if (!VisualizeMapsActivity.getMapsDB().contains(
						new MapFromString(mFileList[i], "Local"))) {
					VisualizeMapsActivity.getMapsDB().add(
							new MapFromString(mFileList[i], "Local"));
					Log.v("addMapsFromLocalStorage", "It is a new local map: "
							+ mFileList[i]);
				} else {
					Log.e("addMapsFromLocalStorage",
							"Maps already in the database " + mFileList[i]);
				}
			} catch (Exception e) {
				Log.e("addMapsFromLocalStorage", "Maps string incorrect "
						+ mFileList[i]);
			}
		}

	}

}
