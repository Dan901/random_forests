package hr.fer.zemris.ml.model.decision_tree;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import hr.fer.zemris.ml.model.IPredictor;
import hr.fer.zemris.ml.model.data.Sample;

public class DecisionTree<T> implements IPredictor<T>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private Node<T> root;
	private List<Sample<T>> trainingSamples;

	public DecisionTree(Node<T> root, List<Sample<T>> trainingSamples) {
		this.root = Objects.requireNonNull(root);
		this.trainingSamples = Objects.requireNonNull(trainingSamples);
	}

	public Node<T> getRoot() {
		return root;
	}

	public List<Sample<T>> getTrainingSamples() {
		return trainingSamples;
	}

	public int getDepth() {
		return root.getDepth();
	}

	@Override
	public T predict(double[] features) {
		return root.getTargetValue(features);
	}
}
