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

/**
 * Classification random forest model. Target class is represented with a
 * {@code String} value.
 *
 * @author Dan
 */
public class ClassificationRandomForest extends RandomForest<String> {

	/**
	 * Loads the serialized model from the given file.
	 * 
	 * @param file path to the file
	 * @return random forest model
	 * @throws IOException if I/O error occurs
	 * @throws ClassNotFoundException if given file doesn't contain the
	 *         appropriate information
	 */
	public static ClassificationRandomForest loadFromFile(Path file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)));) {
			return (ClassificationRandomForest) ois.readObject();
		}
	}

	private static final long serialVersionUID = 1L;

	private List<String> allClasses;

	/**
	 * Creates a new random forest model with given decision trees, but discards
	 * their training samples for easier serialization of the model.
	 * 
	 * @param trees decision trees
	 * @param numOfFeatures number of features training samples had
	 * @param allClasses all possible class values
	 */
	public ClassificationRandomForest(List<DecisionTree<String>> trees, int numOfFeatures, List<String> allClasses) {
		super(trees, numOfFeatures);
		this.allClasses = allClasses;
	}

	/**
	 * Returns the mode of classes collected by prediction from every decision
	 * tree in this model.
	 */
	@Override
	public String predict(double[] features) {
		return calculateFrequencies(features).entrySet().stream().max(Comparator.comparingLong(Entry::getValue)).get()
				.getKey();
	}

	public List<String> getAllClasses() {
		return Collections.unmodifiableList(allClasses);
	}

	/**
	 * Asks every decision tree in this model for a prediction based on given
	 * features and returns an array with probabilities for each class.
	 * <p>
	 * Value at index {@code i} is probability that given features correspond to
	 * class at index {@code i} in the {@code List} returned by
	 * {@link #getAllClasses()}. If none of the trees vote for a class, the
	 * probability at that index will be 0.
	 * <p>
	 * {@link #predict(double[])} will return the class with the highest
	 * probability.
	 * 
	 * @param features features to consider for prediction
	 * @return an {@code array} of probabilities
	 */
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
		return trees.stream().map(tree -> tree.getTargetValue(features))
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
	}
}
