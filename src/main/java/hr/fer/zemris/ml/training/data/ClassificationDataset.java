package hr.fer.zemris.ml.training.data;

import java.util.Set;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;

public class ClassificationDataset extends Dataset<String> {

	@Override
	protected String parseTargetVariable(String target) {
		return target;
	}

	public Set<String> getAllClasses() {
		return samples.stream().map(Sample::getTarget).collect(Collectors.toSet());
	}
}
