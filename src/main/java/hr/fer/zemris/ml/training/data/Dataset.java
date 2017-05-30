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

public abstract class Dataset<T> {

	private static final String COMMENT = "#";

	protected List<Sample<T>> samples;
	protected int numOfFeatures;
	protected List<String> variableNames;

	public Dataset() {
		samples = new ArrayList<>();
	}

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
