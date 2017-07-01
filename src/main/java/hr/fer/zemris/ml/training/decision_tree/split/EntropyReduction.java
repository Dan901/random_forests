package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Map;

import hr.fer.zemris.ml.training.data.Dataset;

/**
 * Chooses the best split based on entropy reduction (i.e. maximizing
 * information gain). Usable for classification tasks.
 * <p>
 * Splitting of samples stops when all the current samples belong to the same
 * class.
 *
 * @author Dan
 */
public class EntropyReduction extends AbstractClassificationSplitCriterion {

	public EntropyReduction(Dataset<String> dataset, int minSamplesPerNode) {
		super(dataset, minSamplesPerNode);
	}

	@Override
	protected double groupValue(Map<String, Integer> classCount, int groupSize) {
		double log2 = Math.log10(2);
		return classCount.values().stream().mapToDouble(c -> c / (double) groupSize).map(p -> -p * Math.log10(p) / log2)
				.sum();
	}
}
