package hr.fer.zemris.ml.model.decision_tree;

/**
 * Terminal node in a regression decision tree. {@code Double} value given in
 * the constructor is always returned as a prediction for given features.
 *
 * @author Dan
 */
public class AverageValueTerminalNode extends Node<Double> {

	private static final long serialVersionUID = 1L;

	private double value;

	/**
	 * Constructs a new {@code AverageValueTerminalNode}.
	 * 
	 * @param value target variable value to return as a predction
	 */
	public AverageValueTerminalNode(double value) {
		this.value = value;
	}

	@Override
	public Double getTargetValue(double[] features) {
		return value;
	}

	@Override
	public int getDepth() {
		return 0;
	}

	@Override
	public String toString() {
		return Double.toString(value);
	}
}
