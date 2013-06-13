package org.serval.servalmaps.fieldtracer.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.serval.servalmaps.fieldtracer.SettingsActivity;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

public class FileTools {

	// Copy of the files listed in the Android assets folder
	public static void copyAssets(Context myContext) {
		AssetManager assetManager = myContext.getAssets();
		String[] files = null;
		try {
			files = assetManager.list("");
		} catch (IOException e) {
			Log.e("tag", "Failed to get asset file list.", e);
		}
		for (String filename : files) {
			InputStream in = null;
			OutputStream out = null;
			try {
				in = assetManager.open(filename);
				out = new FileOutputStream(Environment
						.getExternalStorageDirectory().getPath()
						+ SettingsActivity.APP_NAME_PATH + filename);
				FileTools.copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (IOException e) {
				Log.e("tag", "Failed to copy asset file: " + filename, e);
			}
		}
	}

	// Creation of the passed directory if they don't exist
	public static boolean createDirIfNotExists(String path) {
		boolean ret = true;

		File file = new File(Environment.getExternalStorageDirectory(), path);
		if (!file.exists()) {
			if (!file.mkdirs()) {
				Log.e("Log :: ", "Problem creating folder");
				ret = false;
			}
		}
		return ret;

	}

	public static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

	public static void saveTraceObject(Object o) {
		try {
			String path = Environment.getExternalStorageDirectory()
					+ SettingsActivity.APP_NAME_PATH + "temp/"
					+ "save_last_trace";
			FileOutputStream file = new FileOutputStream(path);
			ObjectOutputStream oos = new ObjectOutputStream(file);
			oos.writeObject(o);
			oos.flush();
			oos.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	public static Object retrieveTraceObject() {
		Object object = null;
		try {
			String path = Environment.getExternalStorageDirectory()
					+ SettingsActivity.APP_NAME_PATH + "temp/"
					+ "save_last_trace";
			FileInputStream file = new FileInputStream(path);
			ObjectInputStream ois = new ObjectInputStream(file);
			object = (Object) ois.readObject();
			ois.close();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return object;
	}

}
