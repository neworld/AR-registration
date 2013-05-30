package lt.neworld.arRegistration.prediction;

import java.util.List;

import android.util.SparseArray;

import lt.neworld.arRegistration.Cluster;

public class AlphaBetaGammaPredictor implements Predictor {
	
	private SparseArray<ABGFilter> filters = new SparseArray<ABGFilter>();
	
	@Override
	public void predict(List<Cluster> clusters, double T) {
		for (Cluster feature : clusters) {
			ABGFilter filter = filters.get(feature.id);
			if (filter == null) {
				filter = new ABGFilter(feature);
				filters.put(feature.id, filter);
			} else {
				filter.correct(feature, T);
			}
		}
	}

}

class ABGFilter {
	private double lastX, lastY;
	private double lastVX, lastVY;
	private double lastAX, lastAY;
	
	private static final double ALPHA = 0.8;
	private static final double BETA = 0.8;
	private static final double GAMMA = 0.25;
	
	public ABGFilter (Cluster cluster) {
		lastX = cluster.getX();
		lastY = cluster.getY();
	}
	
	public void correct(Cluster cluster, double T) {
		double x = lastX + T * lastVX + .5 * T * T * lastAX;
		double y = lastY + T * lastVY + .5 * T * T * lastAY;
		double vx = lastVX + T * lastAX;
		double vy = lastVY + T * lastAY;
		
		double deltaX = cluster.getX() - x;
		double deltaY = cluster.getY() - y;
		
		lastX = x + ALPHA * deltaX;
		lastY = y + ALPHA * deltaY;
		
		lastVX = vx + BETA / T * deltaX;
		lastVY = vy + BETA / T * deltaY;
		
		lastAX += GAMMA / T / T * deltaX;
		lastAY += GAMMA / T / T * deltaY;
		
		cluster.correct((int)lastX, (int)lastY);
	}
}