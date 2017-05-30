package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Collection;
import java.util.List;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.training.data.Dataset;

public class SDReduction extends AbstractSplitCriterion<Double> {

	private static final double MIN_SD_PERCENT = 0.05;
	private double minSD;

	public SDReduction(Dataset<Double> dataset, int minSamplesPerNode) {
		super(dataset, minSamplesPerNode);
		minSD = calculateSD(dataset.getSamples()) * MIN_SD_PERCENT;
	}

	@Override
	public double calculateValueToMinimize(Collection<List<Sample<Double>>> groups) {
		int n = 0;
		double sd = 0;
		for (List<Sample<Double>> group : groups) {
			int size = group.size();
			sd += calculateSD(group) * size;
			n += size;
		}
		return sd / n;
	}

	@Override
	public boolean dontSplit(List<Sample<Double>> samples) {
		return calculateSD(samples) <= minSD;
	}

	private double calculateSD(Collection<Sample<Double>> samples) {
		if (samples.isEmpty()) {
			return 0;
		}
		double avg = samples.stream().mapToDouble(Sample::getTarget).average().getAsDouble();
		double sum = samples.stream().mapToDouble(Sample::getTarget).map(x -> x * x).sum();
		int n = samples.size();
		return Math.sqrt((sum - n * avg * avg) / n);
	}
}
