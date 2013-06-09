package org.serval.servalmaps.fieldtracer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.serval.servalmaps.fieldtracer.utils.BackgroundMaps;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class ToolsActivity extends Activity {
	
	private String[] mFileList;
	private static File mPath = new File(Environment.getExternalStorageDirectory(),"/_FieldTracer/");
	private String mChosenFile;
	private static final String FTYPE = ".map"; //if we want an extension filter
	private static final int DIALOG_LOAD_MAPS = 2000;

	//Vector used to store users maps information
	private Vector <String> users_maps_name = new Vector<String>();
	private Vector <Uri> users_maps_uri = new Vector<Uri>();
	private Vector <Integer> users_maps_size = new Vector<Integer>();
	private static Integer index; // Index for user

	private static final String TAG = "Debug";	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tools);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tools, menu);
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
	
	
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new Builder(this);

	    switch(id) {
	        case DIALOG_LOAD_MAPS:
	            builder.setTitle("Choose a map from an user");
	            if(mFileList == null) {
	                Log.e(TAG, "Showing file picker before loading the file list");
	                dialog = builder.create();
	                return dialog;
	            }
	            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    mChosenFile = mFileList[which];
	                    index=which;
	                    Log.v("BackgroundMaps", "Chosen file was: " + mChosenFile + " at index: " + index.toString());
	                    Log.v("BackgroundMaps", "users_maps_uri.elementAt(index) was: " + users_maps_uri.elementAt(index).toString() + " and users_maps_name.elementAt(index): " + users_maps_name.elementAt(index));
	                    executeCopy(users_maps_uri.elementAt(index), users_maps_name.elementAt(index));
	                }
	            });
	            break;
	    }
	    dialog = builder.show();
	    return dialog;
	}
	
	public void ButtonOnGetMaps(View v){
		
		BackgroundMaps bg_maps = new BackgroundMaps();
		bg_maps.refreshType(".map", this.getContentResolver());
		
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
		}	
		
		mFileList = new String[ maps_file.size() ];
		maps_file.toArray( mFileList );
		this.onCreateDialog(DIALOG_LOAD_MAPS);	
	}
	
	public void ButtonOnVisualize(View v){
		Intent intent_trace = new Intent(ToolsActivity.this,VisualizeMapsActivity.class);
		ToolsActivity.this.startActivity(intent_trace);
	}
	
	public static long copyLarge(InputStream input, OutputStream output) throws IOException 
	{
	  byte[] buffer = new byte[4096];
	  long count = 0L;
	  int n = 0;
	  while (-1 != (n = input.read(buffer))) {
	   output.write(buffer, 0, n);
	   count += n;
	  }
	  return count;
	}
	
	public void executeCopy(Uri uri, String s){
		try {
			ContentResolver content_resolver = this.getContentResolver();
			InputStream mInputStream = content_resolver.openInputStream(uri);
			File file = new File(mPath + "/" + s);
			OutputStream outputstream = new FileOutputStream(file);
			
			try {
				copyLarge(mInputStream, outputstream);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("BackgroundMaps", "Copy failed with: " + s + " and URI: " + uri.toString());
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.e("BackgroundMaps", "Input Stream failed with file: " + s + " and URI: " + uri.toString());
		}
	}

}
