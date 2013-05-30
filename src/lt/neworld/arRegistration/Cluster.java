package lt.neworld.arRegistration;

import android.graphics.Rect;

public class Cluster {
	public int id, minX, minY, maxX, maxY;
	
	private int oriX;
	private int oriY;
	
	public Cluster(int id, int minX, int minY, int maxX, int maxY) {
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
	
	public int calcDif(Cluster feature) {
		int diffX = feature.oriX - oriX;
		int diffY = feature.oriY - oriY;
		
		return (int) Math.sqrt(diffX * diffX + diffY * diffY);
	}
	
	public double correctionPercent(Cluster feature) {
		int diffX = feature.oriX - oriX;
		int diffY = feature.oriY - oriY;
		
		double measuredDistance = Math.sqrt(diffX * diffX + diffY * diffY);
		
		diffX = feature.oriX - getX();
		diffY = feature.oriY - getY();
		
		double distance = Math.sqrt(diffX * diffX + diffY * diffY);
		
		if (Math.abs(measuredDistance) < 5)
			return measuredDistance < distance? 0 : 1;
		
		return 1 - distance / measuredDistance;
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
