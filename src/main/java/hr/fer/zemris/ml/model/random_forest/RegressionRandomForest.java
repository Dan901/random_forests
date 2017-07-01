package hr.fer.zemris.ml.model.random_forest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import hr.fer.zemris.ml.model.decision_tree.DecisionTree;

public class RegressionRandomForest extends RandomForest<Double> implements Serializable {

	/**
	 * Loads the serialized model from the given file.
	 * 
	 * @param file path to the file
	 * @return random forest model
	 * @throws IOException if I/O error occurs
	 * @throws ClassNotFoundException if given file doesn't contain the
	 *         appropriate information
	 */
	public static RegressionRandomForest loadFromFile(Path file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)));) {
			return (RegressionRandomForest) ois.readObject();
		}
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new random forest model with given decision trees, but discards
	 * their training samples for easier serialization of the model.
	 * 
	 * @param trees decision trees
	 * @param numOfFeatures number of features training samples had
	 */
	public RegressionRandomForest(List<DecisionTree<Double>> trees, int numOfFeatures) {
		super(trees, numOfFeatures);
	}

	/**
	 * Returns the average value of values collected from predictions by every
	 * tree in this model.
	 */
	@Override
	public Double predict(double[] features) {
		checkNumOfFeatures(Objects.requireNonNull(features));
		return trees.stream().mapToDouble(tree -> tree.getTargetValue(features)).average().getAsDouble();
	}

}
