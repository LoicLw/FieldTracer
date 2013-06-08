package org.serval.servalmaps.fieldtracer.utils;

import org.mapsforge.core.model.GeoPoint;

public class BoundaryBox {


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((lower_right == null) ? 0 : lower_right.hashCode());
		result = prime * result
				+ ((upper_left == null) ? 0 : upper_left.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoundaryBox other = (BoundaryBox) obj;
		if (lower_right == null) {
			if (other.lower_right != null)
				return false;
		} else if (!lower_right.equals(other.lower_right))
			return false;
		if (upper_left == null) {
			if (other.upper_left != null)
				return false;
		} else if (!upper_left.equals(other.upper_left))
			return false;
		return true;
	}

	private GeoPoint upper_left;
	private GeoPoint lower_right;
	
	BoundaryBox(GeoPoint upper_left, GeoPoint lower_right){
		this.lower_right=lower_right;
		this.upper_left=upper_left;
	}

	public GeoPoint getUpper_left() {
		return upper_left;
	}

	public void setUpper_left(GeoPoint upper_left) {
		this.upper_left = upper_left;
	}

	public GeoPoint getLower_right() {
		return lower_right;
	}

	public void setLower_right(GeoPoint lower_right) {
		this.lower_right = lower_right;
	}
}
