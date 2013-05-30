package lt.neworld.arRegistration.prediction;

import java.util.List;

import lt.neworld.arRegistration.Cluster;

public interface Predictor {
	public void predict(List<Cluster> clusters, double T);
}
