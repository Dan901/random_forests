package hr.fer.zemris.ml.model.decision_tree;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Represents the inner node of the decision tree. Supports only features of
 * type {@code double} and that is why the node is always binary.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public class BinaryNode<T> extends Node<T> {

	private static final long serialVersionUID = 1L;

	private Node<T> left;
	private Node<T> right;
	private Predicate<double[]> split;

	public BinaryNode(Node<T> left, Node<T> right, Predicate<double[]> split) {
		this.left = Objects.requireNonNull(left);
		this.right = Objects.requireNonNull(right);
		this.split = Objects.requireNonNull(split);
	}

	@Override
	public T getTargetValue(double[] features) {
		if (split.test(features)) {
			return left.getTargetValue(features);
		} else {
			return right.getTargetValue(features);
		}
	}

	@Override
	public int getDepth() {
		return 1 + Math.max(left.getDepth(), right.getDepth());
	}

	@Override
	public String toString() {
		return split.toString();
	}
}
