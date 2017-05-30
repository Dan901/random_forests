package hr.fer.zemris.ml.model.random_forest;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.IPredictor;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.model.decision_tree.Node;

public abstract class RandomForest<T> implements IPredictor<T>, Serializable {

	private static final long serialVersionUID = 1L;

	protected List<Node<T>> trees;
	private int numOfFeatures;

	public RandomForest(List<DecisionTree<T>> trees, int numOfFeatures) {
		if (trees == null || trees.isEmpty()) {
			throw new IllegalArgumentException("Forest has to have at least 1 tree.");
		}
		this.trees = trees.stream().map(DecisionTree::getRoot).collect(Collectors.toList());
		this.numOfFeatures = numOfFeatures;
	}

	public int getNumOfTrees() {
		return trees.size();
	}

	public void saveToFile(Path file) throws IOException {
		try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(Files.newOutputStream(file)));) {
			oos.writeObject(this);
			oos.flush();
		}
	}

	protected void checkNumOfFeatures(double[] features) {
		if (features.length != numOfFeatures) {
			throw new IllegalArgumentException(
					"Cannot make a prediction. Expected number of featrues is: " + numOfFeatures);
		}
	}
}
