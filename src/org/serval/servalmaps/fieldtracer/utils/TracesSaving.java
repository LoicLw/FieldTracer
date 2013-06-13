package org.serval.servalmaps.fieldtracer.utils;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.serval.servalmaps.fieldtracer.SettingsActivity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

public class TracesSaving{

	public static void writePOI(Double longitude, Double latitude,
			Float accuracy, String name, String poi_type, Activity activity) {
		String separator = System.getProperty("line.separator");
		String str = "";
		// Get the time to date the POI
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		// Writing file to SD card
		File file = new File(Environment.getExternalStorageDirectory()
				+ SettingsActivity.APP_NAME_PATH, name.replaceAll(" ", "_")
				+ "_" + poi_type.replaceAll(" ", "_") + "_"
				+ today.format("%Y%m%d-%H-%M-%S") + ".poi");
		try {
			FileWriter fWriter = new FileWriter(file, true);
			str = longitude + "," + latitude + "," + accuracy + "," + name
					+ ',';
			fWriter.append(str.toString().trim());
			fWriter.append(separator);
			fWriter.close();
		} catch (Exception e) {
			Log.e("Error while trying to write POI to SDCard", e.getMessage());
		}
		
		addToServalRhizome(file.toString(), activity);
		
	}

	public static void writeTraceText(Double longitude, Double latitude,
			Float accuracy, String name) {
		String separator = System.getProperty("line.separator");
		String str = "";
		// Get the time to date the Trace
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		// Writing file to SD card
		File file = new File(Environment.getExternalStorageDirectory()
				+ SettingsActivity.APP_NAME_PATH, "Trace_"
				+ name.replaceAll(" ", "_") + "_" + today.format("%Y%m%d")
				+ ".trace");
		try {
			FileWriter fWriter = new FileWriter(file, true);
			str = longitude + "," + latitude + "," + accuracy + "," + name;
			fWriter.append(str.toString().trim());
			fWriter.append(separator);
			fWriter.close();
		} catch (Exception e) {
			Log.e("Error while trying to write text trace to SDCard",
					e.getMessage());
		}
	}

	public static void closeTraceGPX(String name, Activity activity) {
		String separator = System.getProperty("line.separator");
		// Get the time to date the Trace
		Time today = new Time(Time.getCurrentTimezone());
		today.setToNow();
		// Writing file to SD card
		File file = new File(Environment.getExternalStorageDirectory()
				+ SettingsActivity.APP_NAME_PATH, "Trace_"
				+ name.replaceAll(" ", "_") + "_" + today.format("%Y%m%d")
				+ ".gpx");
		try {
			FileWriter fWriter = new FileWriter(file, true);
			if (file.exists()) {
				fWriter.append("</trkseg></trk></gpx>" + separator);
				fWriter.close();
			}
		} catch (Exception e) {
			Log.e("Error while trying to write the latest part of the GPX file",
					e.getMessage());
		}
		
		addToServalRhizome(file.toString(), activity);
	}

	public static void writeTraceGPX(Double longitude, Double latitude,
			Float accuracy, String name, String trace_type) {
		String separator = System.getProperty("line.separator");
		// The time format associated with each needs to be conformed to ISO
		// 8601 specification in UTC time
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		Time today = new Time(Time.getCurrentTimezone());
		// Each trackpoint needs its own time
		today.setToNow();
		// Writing file to SD card
		File file = new File(Environment.getExternalStorageDirectory()
				+ SettingsActivity.APP_NAME_PATH, "Trace_"
				+ name.replaceAll(" ", "_") + "_" + today.format("%Y%m%d")
				+ ".gpx");
		try {
			FileWriter fWriter = new FileWriter(file, true);
			if (file.length() == 0) {
				fWriter.append("<?xml version=\"1.0\" "
						+ "encoding=\"UTF-8\"?>"
						+ "\n"
						+ "<gpx version=\"1.0\" "
						+ "creator=\"FieldTracer\"  "
						+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.topografix.com/GPX/1/0\""
						+ " xsi:schemaLocation=\"http://www.topografix.com/GPX/1/0 http://www.topografix.com/GPX/1/0/gpx.xsd\">"
						+ separator + "<metadata><name>\"" + trace_type
						+ "\"</name></metadata>" + separator + "<trk><name>\""
						+ name + "\"</name><trkseg>" + separator);
			}
			String nowAsString = df.format(new Date());
			fWriter.append("<trkpt lat=\"" + latitude + "\" lon=\"" + longitude
					+ "\"><time>" + nowAsString.replace("+0000", "Z")
					+ "</time></trkpt>" + separator);
			fWriter.close();
		} catch (Exception e) {
			Log.e("Error while trying to write GPX to SDCard", e.getMessage());
		}
		
	}
	
	public static void addToServalRhizome(String path, Activity activity){
		SharedPreferences settings = activity.getSharedPreferences("UserInfo", 0);
		if (settings.getString("AutomatedTracesSharing", "").toString().equalsIgnoreCase("True")){
			ServalRhizomeTools.addFile(activity.getBaseContext(),path);
		}
	}

}
