package org.serval.servalmaps.fieldtracer.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class TextDrawable extends Drawable {

	    private final String text;
	    private final Paint paint;

	    public TextDrawable(String text, int col) {

	        this.text = text;

	        this.paint = new Paint();
	        paint.setColor(col);
	        paint.setTextSize(42f);
	        paint.setAntiAlias(true);
	        paint.setFakeBoldText(true);
	        paint.setShadowLayer(6f, 0, 0, Color.WHITE);
	        //paint.setStyle(Paint.Style.FILL);
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
	}
