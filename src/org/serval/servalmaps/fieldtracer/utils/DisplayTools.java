package org.serval.servalmaps.fieldtracer.utils;

import java.util.Vector;

import org.serval.servalmaps.fieldtracer.R;

import android.view.View;
import android.widget.CheckBox;

public class DisplayTools {
	public static String getCheckBoxText(View promptsView) {
		String boxText = "";
		Vector<CheckBox> vect = new Vector<CheckBox>();
		vect.addElement((CheckBox) promptsView.findViewById(R.id.checkBox1));
		vect.addElement((CheckBox) promptsView.findViewById(R.id.checkBox2));
		vect.addElement((CheckBox) promptsView.findViewById(R.id.checkBox3));
		vect.addElement((CheckBox) promptsView.findViewById(R.id.checkBox4));
		vect.addElement((CheckBox) promptsView.findViewById(R.id.checkBox5));

		for (int i = 0; i < vect.size(); i++) {
			if (vect.get(i).isChecked()) {
				if (boxText == "") {
					boxText = vect.get(i).getText().toString();
				} else {
					boxText = boxText + "," + vect.get(i).getText().toString();
				}
			}
		}
		return boxText;
	}
	
	

}
