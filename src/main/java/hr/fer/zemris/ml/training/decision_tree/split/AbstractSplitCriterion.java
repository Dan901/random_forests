package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.SplitPredicate;
import hr.fer.zemris.ml.training.data.Dataset;

/**
 * Abstract class for different split criteria. All criteria share the same code
 * that evaluates something on every possible split and that functionality is
 * extracted here.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public abstract class AbstractSplitCriterion<T> implements ISplitCriterion<T> {

	private int minSamplesPerNode;

	public AbstractSplitCriterion(Dataset<T> dataset, int minSamplesPerNode) {
		if (minSamplesPerNode < 1) {
			throw new IllegalArgumentException("Minimum number of samples per node cannot be less than 1.");
		}
		this.minSamplesPerNode = minSamplesPerNode;
	}

	@Override
	public Predicate<double[]> getBestSplit(List<Sample<T>> samples, List<Integer> featureIndices) {
		if (samples.isEmpty()) {
			throw new IllegalArgumentException("Samples list is empty.");
		}

		if (dontSplit(samples)) {
			return (s -> true);
		}

		SplitPredicate current = new SplitPredicate();
		SplitPredicate bestSplit = null;
		Double minValue = null;

		for (Integer feature : featureIndices) {
			current.featureIndex = feature;
			for (Sample<T> sample : samples) {
				current.threshold = sample.getFeature(feature);
				Map<Boolean, List<Sample<T>>> split = samples.stream()
						.collect(Collectors.partitioningBy(s -> current.test(s.getFeatures())));
				if (split.get(true).size() < minSamplesPerNode || split.get(false).size() < minSamplesPerNode) {
					continue;
				}
				double value = calculateValueToMinimize(split.values());

				if (bestSplit == null) {
					bestSplit = new SplitPredicate();
				}
				if (minValue == null || value < minValue) {
					minValue = value;
					bestSplit.featureIndex = current.featureIndex;
					bestSplit.threshold = current.threshold;
				}
				if (minValue == 0) {
					break;
				}
			}
		}

		return bestSplit == null ? (s -> true) : bestSplit;
	}

	abstract protected boolean dontSplit(List<Sample<T>> samples);

	abstract protected double calculateValueToMinimize(Collection<List<Sample<T>>> groups);
}
