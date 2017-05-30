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

	public static RegressionRandomForest loadFromFile(Path file) throws IOException, ClassNotFoundException {
		try (ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(file)));) {
			return (RegressionRandomForest) ois.readObject();
		}
	}

	private static final long serialVersionUID = 1L;

	public RegressionRandomForest(List<DecisionTree<Double>> trees, int numOfFeatures) {
		super(trees, numOfFeatures);
	}

	@Override
	public Double predict(double[] features) {
		checkNumOfFeatures(Objects.requireNonNull(features));
		return trees.stream().mapToDouble(tree -> tree.predict(features)).average().getAsDouble();
	}

}
