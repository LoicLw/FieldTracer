package org.serval.servalmaps.fieldtracer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.GeoPoint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class TextDrawer extends Drawable {

	private final String text;
	private final Paint paint;

	public TextDrawer(String text, int col) {

		this.text = text;

		this.paint = new Paint();
		paint.setColor(col);
		paint.setTextSize(42f);
		paint.setAntiAlias(true);
		paint.setFakeBoldText(true);
		paint.setShadowLayer(4f, 0, 0, Color.WHITE);
		// paint.setStyle(Paint.Style.FILL);
		paint.setTextAlign(Paint.Align.LEFT);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawText(text, 10, 50, paint);
	}

	@Override
	public void setAlpha(int alpha) {
		paint.setAlpha(alpha);
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		paint.setColorFilter(cf);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	static public void drawTextOnMap(String name, Double lati, Double longi,
			Integer color, MapView mapView, File cacheDir) {
		// Drawing the map name as a drawable since Mapsforge does not
		// provide a way to do it in 0.3.1
		Bitmap b = Bitmap.createBitmap(55 + 20 * name.length(), 75,
				Bitmap.Config.ARGB_8888);
		Canvas can = new Canvas(b);

		Drawable point2_drawable;
		point2_drawable = new TextDrawer(name, color);
		point2_drawable.draw(can);

		// Writing the drawable as a bitmap to disk
		try {
			File file = new File(cacheDir + "/" + name + ".png");
			FileOutputStream out = new FileOutputStream(file);
			b.compress(Bitmap.CompressFormat.PNG, 90, out);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Reading and displaying it
		String pathName = cacheDir + "/" + name + ".png";
		Drawable file_drawable = Drawable.createFromPath(pathName);

		file_drawable = Marker.boundCenter(file_drawable);

		GeoPoint geoPoint = new GeoPoint(lati, longi);
		Marker marker = new Marker(geoPoint, file_drawable);

		ListOverlay listOverlay = new ListOverlay();
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();

		overlayItems.add(marker);

		mapView.getOverlays().add(listOverlay);
	}
}
