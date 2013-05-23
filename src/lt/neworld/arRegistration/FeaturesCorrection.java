package lt.neworld.arRegistration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class FeaturesCorrection {
	private List<Feature> lastFeatures;
	
	public void calculateFeatures(List<Feature> features) {
		HashSet<Integer> used = new HashSet<Integer>();
		
		List<Feature> newFeatures = new ArrayList<Feature>(features);
		
		if (lastFeatures != null) {
			while (newFeatures.size() > 0 && lastFeatures.size() > 0) {
				Feature oldFeature = null;
				Feature newFeature = null;
				int min = -1;
				
				for (Feature nf : newFeatures) {
					for (Feature feature : lastFeatures) {
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
					}
				}
			}
			
			int index = 0;
			
			for (Feature feature : newFeatures) {
				while (true) {
					index++;
					if (!used.contains(index)) {
						feature.id = index;
						break;
					}
				}
			}
		}
		
		lastFeatures = new ArrayList<Feature>(features);
	}
}
