package org.serval.servalmaps.fieldtracer;

import java.util.Vector;

import org.mapsforge.core.model.GeoPoint;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;



public class BackgroundMaps extends Application {
	
	private Vector <String> maps_name_from_users = new Vector<String>();
	private Vector <Uri> maps_uri_from_users = new Vector<Uri>();
	private Vector <Integer> maps_size_from_users = new Vector<Integer>();
	
	 public Vector<String> getMaps_name_from_users() {
		return maps_name_from_users;
	}

	 public Vector<Uri> getMaps_uri_from_users() {
		return maps_uri_from_users;
	}	 

	 public Vector<Integer> getMaps_size_from_users() {
		return maps_size_from_users;
	}	
	 
	public void refreshType(String type, ContentResolver content){
	     Uri manifests = Uri.parse("content://org.servalproject.files/");
	     //Cursor c = this.getContentResolver().query(manifests, null, null, new String[]{"file","%"+type}, null);
	     Cursor c = content.query(manifests, null, null, new String[]{"file","%"+type}, null);
	     if (c==null)
	       return;
	     try{
	       int name_col=c.getColumnIndexOrThrow("name");
	       int id_col = c.getColumnIndexOrThrow("id");
	       int size_col = c.getColumnIndexOrThrow("filesize");
	       while(c.moveToNext()){
	         String name=c.getString(name_col);
	         byte []id=c.getBlob(id_col);
	         String id_str = binToHex(id,0,id.length);
	         Uri uri = Uri.parse("content://org.servalproject.files/"+id_str);
	         Integer size=c.getInt(size_col);
	         maps_name_from_users.add(name);
	         maps_uri_from_users.add(uri);
	         maps_size_from_users.add(size);
	         //processFile(name, uri);
	       }
	     }finally{
	       c.close();
	     }
	   }
 
   public void processFile(String fileName, Uri uri){
	    // see if the file is one we want to work with
	       if (fileName==null){
		        Log.e("BackgroundMaps", "Filename is null");
		        return;
	    }
	       //For future direction: getting the Boundary Box and choosing one map
	       //String[] mFileParts = fileName.split("-");
   }
   
   public static String binToHex(byte[] buff, int offset, int len) {
	     StringBuilder sb = new StringBuilder();
	     for (int i = 0; i < len; i++) {
	       sb.append(Character.forDigit(((buff[i + offset]) & 0xf0) >> 4, 16));
	       sb.append(Character.forDigit((buff[i + offset]) & 0x0f, 16));
	     }
	     return sb.toString().toUpperCase();
	   }

}
