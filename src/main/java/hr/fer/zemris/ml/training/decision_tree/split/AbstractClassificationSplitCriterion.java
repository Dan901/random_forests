package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.training.data.Dataset;

public abstract class AbstractClassificationSplitCriterion extends AbstractSplitCriterion<String> {

	public AbstractClassificationSplitCriterion(Dataset<String> dataset, int minSamplesPerNode) {
		super(dataset, minSamplesPerNode);
	}

	@Override
	protected double calculateValueToMinimize(Collection<List<Sample<String>>> groups) {
		double value = 0;
		int n = 0;
		for (List<Sample<String>> group : groups) {
			int size = group.size();
			if (size == 0) {
				continue;
			}

			Map<String, Integer> classCount = group.stream()
					.collect(Collectors.toMap(Sample::getTarget, s -> 1, Integer::sum));
			double groupValue = groupValue(classCount, size);
			value += size * groupValue;
			n += size;
		}

		return value / n;
	}

	@Override
	protected boolean dontSplit(List<Sample<String>> samples) {
		String firstClass = samples.get(0).getTarget();
		return samples.stream().allMatch(s -> s.getTarget().equals(firstClass));
	}

	abstract protected double groupValue(Map<String, Integer> classCount, int groupSize);
}
