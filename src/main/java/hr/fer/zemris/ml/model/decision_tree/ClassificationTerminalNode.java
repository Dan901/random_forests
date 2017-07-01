package hr.fer.zemris.ml.model.decision_tree;

import java.util.Objects;

/**
 * Terminal node in a classification decision tree. Class value given in the
 * constructor is always returned as a class prediction for given features.
 *
 * @author Dan
 */
public class ClassificationTerminalNode extends Node<String> {

	private static final long serialVersionUID = 1L;

	private String target;

	/**
	 * Constructs a new {@code ClassificationTerminalNode}.
	 * 
	 * @param target class value to return as a predction
	 */
	public ClassificationTerminalNode(String target) {
		this.target = Objects.requireNonNull(target);
	}

	@Override
	public String getTargetValue(double[] features) {
		return target;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public String toString() {
		return target;
	}
}
