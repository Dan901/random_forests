package hr.fer.zemris.ml.model.decision_tree;

import java.io.Serializable;

public abstract class Node<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	abstract public int getDepth();
	
	abstract public T getTargetValue(double[] features);
}
