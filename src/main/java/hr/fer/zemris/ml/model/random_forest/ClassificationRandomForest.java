package hr.fer.zemris.ml.model.random_forest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.decision_tree.DecisionTree;

public class ClassificationRandomForest extends RandomForest<String> {

	public static ClassificationRandomForest loadFromFile(Path file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)));) {
			return (ClassificationRandomForest) ois.readObject();
		}
	}

	private static final long serialVersionUID = 1L;

	private List<String> allClasses;

	public ClassificationRandomForest(List<DecisionTree<String>> trees, int numOfFeatures, List<String> allClasses) {
		super(trees, numOfFeatures);
		this.allClasses = allClasses;
	}

	@Override
	public String predict(double[] features) {
		return calculateFrequencies(features).entrySet().stream().max(Comparator.comparingLong(Entry::getValue)).get()
				.getKey();
	}

	public List<String> getAllClasses() {
		return Collections.unmodifiableList(allClasses);
	}

	public double[] calculateClassProbabilities(double[] features) {
		double[] probabilities = new double[allClasses.size()];
		Map<String, Long> frequencies = calculateFrequencies(features);
		double size = trees.size();
		for (int i = 0; i < probabilities.length; i++) {
			probabilities[i] = frequencies.getOrDefault(allClasses.get(i), 0L) / size;
		}
		return probabilities;
	}

	private Map<String, Long> calculateFrequencies(double[] features) {
		checkNumOfFeatures(Objects.requireNonNull(features));
		return trees.stream().map(tree -> tree.predict(features))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
}
