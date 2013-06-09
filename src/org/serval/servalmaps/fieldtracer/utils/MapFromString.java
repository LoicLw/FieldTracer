package org.serval.servalmaps.fieldtracer.utils;

import java.util.Vector;

import org.mapsforge.core.model.GeoPoint;

public class MapFromString {

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MapFromString other = (MapFromString) obj;
		if (maps_boundary_box == null) {
			if (other.maps_boundary_box != null)
				return false;
		} else if (!maps_boundary_box.equals(other.maps_boundary_box))
			return false;
		if (maps_name == null) {
			if (other.maps_name != null)
				return false;
		} else if (!maps_name.equals(other.maps_name))
			return false;
		//If nothing is different then true
		return true;
	}


	private BoundaryBox maps_boundary_box;	
	private String maps_name;
	private String maps_type;
	
	public BoundaryBox getMaps_boundary_box() {
		return maps_boundary_box;
	}


	public String getMaps_name() {
		return maps_name;
	}


	public String getMaps_type() {
		return maps_type;
	}
	
	
	public MapFromString(String name, String type){
		String[] string_part = name.split("#_");
		String[] coordinates_upper_left = string_part[1].split(",");
		String[] coordinates_lower_right = string_part[2].split(",");
		
		maps_name = string_part[0];
		GeoPoint a = new GeoPoint(Double.parseDouble(coordinates_upper_left[0]),Double.parseDouble(coordinates_upper_left[1]));
		GeoPoint b = new GeoPoint(Double.parseDouble(coordinates_lower_right[0]),Double.parseDouble(coordinates_lower_right[1]));
		maps_boundary_box =  new BoundaryBox(a,b);
		maps_type = type;

	}




	public boolean checkIfExistUser(String map_name, Vector<MapFromString> mapDB){
		return mapDB.contains(new MapFromString(map_name, "User"));
	}
}
