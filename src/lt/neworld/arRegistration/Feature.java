package lt.neworld.arRegistration;

import android.graphics.Rect;

public class Feature {
	public int id, minX, minY, maxX, maxY;
	
	private int oriX;
	private int oriY;
	
	public Feature(int id, int minX, int minY, int maxX, int maxY) {
		this.id = id;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;

		oriX = getX();
		oriY = getY();
	}
	
	public Rect getRect(float scaleX, float scaleY) {
		return new Rect((int)(minX * scaleX), (int)(minY * scaleY), (int)(maxX * scaleX), (int) (maxY * scaleY));
		//return new Rect((int)(minX * 2), (int)(minY * 2), (int)(maxX * 2), (int) (maxY * 2 * scaleY));
	}
	
	public int calcDif(Feature feature) {
		int diffX = feature.oriX - oriX;
		int diffY = feature.oriY - oriY;
		
		return (int) Math.sqrt(diffX * diffX + diffY * diffY);
	}

	@Override
	public String toString() {
		return String.format("[%d %d %d %d]", minX, minY, maxX, maxY);
	}
	
	public int getX() {
		return (minX + maxX) / 2;
	}
	
	public int getY() {
		return (minY + maxY) / 2;
	}
	
	public void correct(int x, int y) {
		minX += x - oriX;
		maxX += x - oriX;
		minY += y - oriY;
		maxY += y - oriY;
	}
}
