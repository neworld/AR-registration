package lt.neworld.arRegistration;

import android.graphics.Rect;

public class Feature {
	public int id, minX, minY, maxX, maxY;
	
	public Feature(int id, int minX, int minY, int maxX, int maxY) {
		this.id = id;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}
	
	public Rect getRect(float scaleX, float scaleY) {
		return new Rect((int)(minX * scaleX), (int)(minY * scaleY), (int)(maxX * scaleX), (int) (maxY * scaleY));
		//return new Rect((int)(minX * 2), (int)(minY * 2), (int)(maxX * 2), (int) (maxY * 2 * scaleY));
	}
	
	public int calcDif(Feature feature) {
		return Math.abs(feature.maxX - maxX) + Math.abs(feature.maxY - maxY) + Math.abs(feature.minX - minX) + Math.abs(feature.minY - minY);
	}

	@Override
	public String toString() {
		return String.format("[%d %d %d %d]", minX, minY, maxX, maxY);
	}
}
