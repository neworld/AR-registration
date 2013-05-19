package lt.neworld.arRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class FeaturesCorrection {
	private List<Feature> lastFeatures;
	
	public void calculateFeatures(List<Feature> features) {
		HashSet<Integer> used = new HashSet<Integer>();
		
		if (lastFeatures != null) {
			for (Feature newFeature : features) {
				Feature oldFeature = null;
				int min = -1;
				Iterator<Feature> iter = lastFeatures.iterator();
				while (iter.hasNext()) {
					Feature feature = iter.next();
					int diff = feature.calcDif(newFeature);
					if (min == -1 || min > diff) {
						min = diff;
						oldFeature = feature;
					}
				}
				
				if (oldFeature != null) {
					newFeature.id = oldFeature.id;
					lastFeatures.remove(oldFeature);
					used.add(oldFeature.id);
				} else {
					newFeature.id = 0;
				}
			}
			
			int index = 0;
			
			for (Feature feature : features) {
				if (feature.id == 0) {
					while (true) {
						index++;
						if (!used.contains(index)) {
							feature.id = index;
							break;
						}
					}
				}
			}
		}
		
		lastFeatures = new ArrayList<Feature>(features);
	}
}
