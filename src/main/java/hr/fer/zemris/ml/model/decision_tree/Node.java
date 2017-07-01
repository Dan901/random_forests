package hr.fer.zemris.ml.model.decision_tree;

import java.io.Serializable;

/**
 * Abstract representation of a decision tree node.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public abstract class Node<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Calculates the depth of this node.
	 * 
	 * @return depth of this node, if this node is the root of the tree, 0 is
	 *         returned
	 */
	abstract public int getDepth();

	/**
	 * Predicts the target value based on given {@code array} of features.
	 * 
	 * @param features features to consider for prediction
	 * @return target value prediction
	 */
	abstract public T getTargetValue(double[] features);
}
