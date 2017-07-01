package hr.fer.zemris.ml.training.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;

/**
 * Abstract representation of a training dataset for supervised learning
 * methods.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public abstract class Dataset<T> {

	private static final String COMMENT = "#";

	protected List<Sample<T>> samples;
	protected int numOfFeatures;
	protected List<String> variableNames;

	public Dataset() {
		samples = new ArrayList<>();
	}

	/**
	 * Adds a single sample to the dataset.
	 * 
	 * @param sample sample to add, has to have the same number of features as
	 *        previously added samples.
	 */
	public void addSample(Sample<T> sample) {
		Objects.requireNonNull(sample, "Sample cannot be null.");
		int n = sample.getNumOfFeatures();
		if (samples.isEmpty()) {
			numOfFeatures = n;
		} else if (n != numOfFeatures) {
			throw new IllegalArgumentException(
					"Invalide sample size: " + (n + 1) + " Expected: " + (numOfFeatures + 1));
		}
		samples.add(sample);
	}

	public int getSize() {
		return samples.size();
	}

	public List<Sample<T>> getSamples() {
		return Collections.unmodifiableList(samples);
	}

	public int getNumOfFeatures() {
		return numOfFeatures;
	}

	public List<String> getVariableNames() {
		return variableNames;
	}

	public void setVariableNames(List<String> variableNames) {
		this.variableNames = variableNames;
	}

	/**
	 * Loads the samples from given file. Format of the file has to be
	 * {@code CSV} with every row representing one sample, first containing
	 * features and target value at the end of the row. All features have to be
	 * {@code double} values.
	 * 
	 * @param csv path to the file
	 * @param headerRow if {@code true} first row will be parsed as names of
	 *        every variable and saved, not considered as one sample
	 * @throws IOException id I/O error occurs
	 * @throws NumberFormatException if some feature cannot be parsed as a real
	 *         number
	 */
	public void loadFromCSV(Path csv, boolean headerRow) throws IOException, NumberFormatException {
		List<String> lines = Files.readAllLines(csv);
		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty() || line.startsWith(COMMENT)) {
				continue;
			}

			if (headerRow) {
				variableNames = Arrays.stream(line.split(",")).collect(Collectors.toList());
				headerRow = false;
				continue;
			}

			String[] elems = line.split(",");
			if (elems.length < 2) {
				throw new IllegalArgumentException("Sample has to have at least 1 feature and a target variable.");
			}
			double[] features = Arrays.stream(elems, 0, elems.length - 1).mapToDouble(Double::parseDouble).toArray();
			Sample<T> sample = new Sample<>(features, parseTargetVariable(elems[elems.length - 1]));
			addSample(sample);
		}
	}

	protected abstract T parseTargetVariable(String target);
}
