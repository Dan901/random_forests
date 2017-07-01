package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Map;

import hr.fer.zemris.ml.training.data.Dataset;

/**
 * Chooses the best split based on <i>Gini</i> index reduction. Usable for
 * classification tasks.
 * <p>
 * Splitting of samples stops when all the current samples belong to the same
 * class.
 *
 * @author Dan
 */
public class GiniIndexReduction extends AbstractClassificationSplitCriterion {

	public GiniIndexReduction(Dataset<String> dataset, int minSamplesPerNode) {
		super(dataset, minSamplesPerNode);
	}

	@Override
	protected double groupValue(Map<String, Integer> classCount, int groupSize) {
		return 1 - classCount.values().stream().mapToDouble(p -> p / (double) groupSize).map(p -> p * p).sum();
	}

}
