package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FileOutputStream;
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
import org.serval.servalmaps.fieldtracer.utils.Map;
import org.serval.servalmaps.fieldtracer.utils.TextDrawable;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

public class ComparemapsActivity extends MapActivity {



	private Double map_upperleftcorner_lat;
	private Double map_upperleftcorner_long;
	private Double map_lowerrightcorner_lat;
	private Double map_lowerrightcorner_long;	
		
	//Maps
	private static Vector <Map> mapsDB = null;
	

	
	private Vector <Vector> maps_boundary_box = new Vector<Vector>();
	private Vector <GeoPoint> maps_upper_left = new Vector<GeoPoint>();
	private Vector <GeoPoint> maps_lower_right = new Vector<GeoPoint>();
	
	private Vector <String> maps_name = new Vector<String>();
	private Vector <String> maps_type = new Vector<String>();
	
	// list of markers
	private MapView mapView;
	private MyLocationOverlay myLocationOverlay;	
	
	private static final String TAG = "CompareMaps Activity";
	
	public static Vector<Map> getMapsDB() {
		if (mapsDB==null){
			mapsDB = new Vector<Map>(); 
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
		setContentView(R.layout.activity_comparemaps);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Mapsforge objects
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		mapView.getMapViewPosition().setCenter(new GeoPoint(0,0));
		
		
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
		File world = new File(Environment.getExternalStorageDirectory().getPath() + "/_FieldTracer/world.map");
		
		if(!world.exists()){
			//TODO copy World.map to "_FieldTracer" folder
		}
		

		String mMapFileName = Environment.getExternalStorageDirectory().getPath() + "/_FieldTracer/world.map";
		if (mMapFileName != "") {
			Toast.makeText(getApplicationContext(), "Map is " + mMapFileName,Toast.LENGTH_SHORT).show();
		} else {
			Log.v(TAG,"------------------" +"Map path error"+ mMapFileName + "---------------------");		
		}			
		
		
		Log.v(TAG,"------------------" +"Map path is : "+ mMapFileName + "---------------------");		
		mapView.setMapFile(new File(mMapFileName));
		//Add the map to the layout
		ViewGroup layout = (ViewGroup) findViewById(R.id.relativeLayout);
		layout.addView(mapView);
		
		addMapsFromServalUsers();
		addMapsFromLocalStorage();
		
		
		String maps_name[] = new String[5];
		maps_name[1]="Carte France#_50.847573,-6.027374#_42.650122,7.947235";
		maps_name[2]="West Australia#_-13.410994,145.005798#_-39.368279,152.388611";
		maps_name[3]="Tropical Asia#_18.812718,90.675659#_-13.923404,159.581909";
		maps_name[4]="Port Augusta and Pirie#_-31.877558,136.931877#_-33.60547,138.546867";
		
		
		if (mapsDB==null){
			mapsDB = new Vector<Map>(); 
		}
		
		for(int i = 1;i<=4;i++){
			Map map = new Map(maps_name[i],"Users");
			mapsDB.add(map);
		}
		
		String maps_name2="LocalMap Augusta#_-31,136#_-33,138";
		Map map2 = new Map(maps_name2,"Local");
		mapsDB.add(map2);
		
		if (!mapsDB.contains(new Map("LocalMap Augusta#_-31,136#_-33,138", "Local"))){
			Log.v(TAG,"------------------It is a new map--------------------");	
		}


	    for(int i=0; i< mapsDB.size(); i++){
	    	Map map_to_draw = mapsDB.get(i);
	    	
	    	map_upperleftcorner_lat = map_to_draw.getMaps_boundary_box().getUpper_left().latitude;
	    	map_upperleftcorner_long = map_to_draw.getMaps_boundary_box().getUpper_left().longitude;
	    	map_lowerrightcorner_lat = map_to_draw.getMaps_boundary_box().getLower_right().latitude;
	    	map_lowerrightcorner_long = map_to_draw.getMaps_boundary_box().getLower_right().longitude;
			String map_name = map_to_draw.getMaps_name();
		
		    //Drawing a rectangle using map boundary box
	    	Vector <GeoPoint> coordinate_vector = new Vector<GeoPoint>();
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,map_upperleftcorner_long));			
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,map_lowerrightcorner_long));			
			coordinate_vector.add(new GeoPoint(map_lowerrightcorner_lat,map_lowerrightcorner_long));			
			coordinate_vector.add(new GeoPoint(map_lowerrightcorner_lat,map_upperleftcorner_long));		
			coordinate_vector.add(new GeoPoint(map_upperleftcorner_lat,map_upperleftcorner_long));	
			
			 Polyline polyline;
			if (mapsDB.get(i).getMaps_type()=="Users"){
				polyline = createPolyline(coordinate_vector, 0xbb0000ff);	
			} else {
				polyline = createPolyline(coordinate_vector, Color.BLACK);
			}
			
			//Drawing the map name as a drawable since Mapsforge does not provide a way to do it in 0.3.1
			Bitmap b = Bitmap.createBitmap(55+20*map_name.length(), 75, Bitmap.Config.ARGB_8888);
		    Canvas can = new Canvas(b);  
		    
		    Drawable point2_drawable;
		    if (mapsDB.get(i).getMaps_type()=="Users"){
		    	point2_drawable = new TextDrawable(map_name, 0xbb0000ff);	
			} else {
				point2_drawable = new TextDrawable(map_name, Color.BLACK);
			}
		    
		    point2_drawable.draw(can); 
		    
		    //Writing the drawable as a bitmap to disk
		    try {
		    	File file = new File(getCacheDir() + "/" + map_name + ".png");
		        FileOutputStream out = new FileOutputStream(file);
		        b.compress(Bitmap.CompressFormat.PNG, 90, out);
			 } catch (Exception e) {
			        e.printStackTrace();
			 }
		    
		    //Reading and displaying it
		    String pathName = getCacheDir() + "/" + map_name + ".png";
		    Drawable file_drawable = Drawable.createFromPath(pathName);
		    
		    file_drawable = Marker.boundCenter(file_drawable);
		    
		    GeoPoint geoPoint = new GeoPoint((map_upperleftcorner_lat+map_lowerrightcorner_lat)/2,(map_upperleftcorner_long+map_lowerrightcorner_long)/2);
		    Marker marker = new Marker(geoPoint, file_drawable);
		    
			ListOverlay listOverlay = new ListOverlay();
			List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
			
			overlayItems.add(polyline);	
		    overlayItems.add(marker);	
		    
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
		getMenuInflater().inflate(R.menu.comparemaps, menu);
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
	
	public void addMapsFromServalUsers(){
		BackgroundMaps bg_maps = new BackgroundMaps();
		bg_maps.refreshType(".map", this.getContentResolver());
		
		//Vector used to store users maps information
		Vector <String> users_maps_name = new Vector<String>();
		Vector <Uri> users_maps_uri = new Vector<Uri>();
		Vector <Integer> users_maps_size = new Vector<Integer>();
		
		users_maps_name = bg_maps.getMaps_name_from_users();
		users_maps_uri = bg_maps.getMaps_uri_from_users();
		users_maps_size = bg_maps.getMaps_size_from_users();
		
		List<String> maps_file = new ArrayList<String>();
		
		for(int i = 0; i < users_maps_name.size(); i++)
		{
			String s = (String)users_maps_name.elementAt(i);
			Uri uri = (Uri)users_maps_uri.elementAt(i);
			Integer size = (Integer)users_maps_size.elementAt(i);
			Log.v("BackgroundMaps", "One map files was: " + s);
			Log.v("BackgroundMaps", "One file URI was: " + uri.toString());
			Log.v("BackgroundMaps", "One file size was: " + size);
			//TODO check for more than just size
			if (size>0){
				Log.v("BackgroundMaps", "This map sounds good " + s);
				
			} else{			
				s="EMPTY MAP_"+s;
			}
			maps_file.add(s);
			try{
				s=s.replace(".map", "");
				if (!ComparemapsActivity.getMapsDB().contains(new Map(s, "User"))){
					ComparemapsActivity.getMapsDB().add(new Map(s, "User"));
					Log.v(TAG,"------------------It is a new user map: " + s + "--------------------");	
				}
			}catch (Exception e){
				Log.e("BackgroundMaps", "Maps string incorrect " + s); 
			}
			
		}	
	}
	
	public void addMapsFromLocalStorage(){
		File mPath = new File(Environment.getExternalStorageDirectory(),"/_FieldTracer/");
		final String FTYPE = ".map";
		String[] mFileList;
		
		if(mPath.exists()) {
	        FilenameFilter filter = new FilenameFilter() {
	            public boolean accept(File dir, String filename) {
	                File sel = new File(dir, filename);
	                return filename.contains(FTYPE) || sel.isDirectory();
	            }
	        };
	        mFileList = mPath.list(filter);
	    }
	    else {
	        mFileList= new String[0];
	    }
		
		for(int i = 0; i < mFileList.length; i++)
		{
			try{
				mFileList[i]=mFileList[i].replace(".map", "");
				if (!ComparemapsActivity.getMapsDB().contains(new Map(mFileList[i], "Local"))){
					ComparemapsActivity.getMapsDB().add(new Map(mFileList[i], "Local"));
					Log.v(TAG,"------------------It is a new local map: "+ mFileList[i]  +"--------------------");	
				}
			}catch (Exception e){
				Log.e("BackgroundMaps", "Maps string incorrect " + mFileList[i]); 
			}
		}
		
	}

}
