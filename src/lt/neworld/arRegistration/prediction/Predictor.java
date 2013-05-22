package lt.neworld.arRegistration.prediction;

import java.util.List;

import lt.neworld.arRegistration.Feature;

public interface Predictor {
	public void predict(List<Feature> features, double T);
}
