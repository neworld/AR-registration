package lt.neworld.arRegistration;

import android.graphics.Rect;

public class Feature {
	public int id, minX, minY, maxX, maxY;
	
	public Feature(int id, int minX, int minY, int maxX, int maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public Rect getRect() {
		return new Rect(minX, minY, maxX, maxY);
	}
}
