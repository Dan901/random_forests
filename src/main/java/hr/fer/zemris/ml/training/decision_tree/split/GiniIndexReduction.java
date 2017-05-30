package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Map;

import hr.fer.zemris.ml.training.data.Dataset;

public class GiniIndexReduction extends AbstractClassificationSplitCriterion {

	public GiniIndexReduction(Dataset<String> dataset, int minSamplesPerNode) {
		super(dataset, minSamplesPerNode);
	}

	@Override
	protected double groupValue(Map<String, Integer> classCount, int groupSize) {
		return 1 - classCount.values().stream().mapToDouble(p -> p / (double) groupSize).map(p -> p * p).sum();
	}

}
