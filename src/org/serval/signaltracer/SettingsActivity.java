package org.serval.signaltracer;

import java.io.File;
import java.io.FilenameFilter;

import org.serval.signaltracer.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SettingsActivity extends Activity {

private String[] mFileList;
private static File mPath = new File(Environment.getExternalStorageDirectory(),"/_FieldTracer/");
private String mChosenFile;
private static final String FTYPE = ""; //if we want an extension filter
private static final int DIALOG_LOAD_FILE = 1000;

private static String mapFile = mPath + "/" + "adelaide.map"; // Default map
private static String tracesRecordingType = "Text"; // Default recording type

private TextView fileChoosed = null;
private TextView recordingTypeChoosed = null;  
private static final String TAG = "Debug";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		setupActionBar();
		
		fileChoosed = (TextView) findViewById(R.id.textView1); 
		fileChoosed.setText(mPath + "/"+ mapFile.replace(mPath.toString() + "/", ""));
		
		recordingTypeChoosed = (TextView) findViewById(R.id.textView2); 
		recordingTypeChoosed.setText(tracesRecordingType);
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
		getMenuInflater().inflate(R.menu.settings, menu);
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
	
	private void loadFileList() {
	    try {
	        mPath.mkdirs();
	    }
	    catch(SecurityException e) {
	        Log.e(TAG, "unable to write on the sd card " + e.toString());
	    }
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
	}

	protected Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    AlertDialog.Builder builder = new Builder(this);

	    switch(id) {
	        case DIALOG_LOAD_FILE:
	            builder.setTitle("Choose your file");
	            if(mFileList == null) {
	                Log.e(TAG, "Showing file picker before loading the file list");
	                dialog = builder.create();
	                return dialog;
	            }
	            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int which) {
	                    mChosenFile = mFileList[which];
	                    //you can do stuff with the file here too
	                    mapFile = mPath + "/" + mChosenFile;
	                    fileChoosed.setText(mapFile);  
	                    
	                }
	            });
	            break;
	    }
	    dialog = builder.show();
	    return dialog;
	}

	public static String getMapFile() {
		return mapFile;
	}
	
	public static String getTracesRecordingType() {
		return tracesRecordingType;
	}


	public void ButtonOnSelectFile(View v){
		// create alert dialog
		loadFileList();
		this.onCreateDialog(DIALOG_LOAD_FILE);
	}
	
	
}
