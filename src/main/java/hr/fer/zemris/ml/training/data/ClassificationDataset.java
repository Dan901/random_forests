package hr.fer.zemris.ml.training.data;

import java.util.Set;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;

/**
 * Classification dataset with target classes represented by a {@code String}.
 *
 * @author Dan
 */
public class ClassificationDataset extends Dataset<String> {

	@Override
	protected String parseTargetVariable(String target) {
		return target;
	}

	/**
	 * Returns all classes in this dataset.
	 * 
	 * @return {@code Set} with all classes.
	 */
	public Set<String> getAllClasses() {
		return samples.stream().map(Sample::getTarget).collect(Collectors.toSet());
	}
}
