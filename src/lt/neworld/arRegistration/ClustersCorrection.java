package lt.neworld.arRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

public class ClustersCorrection {
	private List<Cluster> lastFeatures;
	
	private double percent = 0.0;
	private int steps = 0;
	
	public void calculateClusters(List<Cluster> features) {
		HashSet<Integer> used = new HashSet<Integer>();
		
		List<Cluster> newFeatures = new ArrayList<Cluster>(features);
		
		if (lastFeatures != null) {
			while (newFeatures.size() > 0 && lastFeatures.size() > 0) {
				Cluster oldFeature = null;
				Cluster newFeature = null;
				int min = -1;
				
				for (Cluster nf : newFeatures) {
					for (Cluster feature : lastFeatures) {
						int diff = feature.calcDif(nf);
						if (min == -1 || min > diff) {
							min = diff;
							oldFeature = feature;
							newFeature = nf;
						}
					}
				}
				
				if (oldFeature != null && newFeature != null) {
					newFeature.id = oldFeature.id;
					lastFeatures.remove(oldFeature);
					
					if (!used.contains(oldFeature.id)) {
						newFeatures.remove(newFeature);
						used.add(oldFeature.id);
						if (min >= 5) {
							percent += oldFeature.correctionPercent(newFeature);
							steps++;
						}
					}
				}
			}
			
			int index = 0;
			
			for (Cluster feature : newFeatures) {
				while (true) {
					index++;
					if (!used.contains(index)) {
						feature.id = index;
						break;
					}
				}
			}
		}
		
		if (steps > 20) {
			Log.v("MEASURED", String.format("Correction: %.2f%%", (percent * 100) / steps));
			percent = 0;
			steps = 0;
		}
		
		lastFeatures = new ArrayList<Cluster>(features);
	}
}
