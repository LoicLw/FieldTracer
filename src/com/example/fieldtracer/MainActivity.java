package com.example.fieldtracer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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

}
