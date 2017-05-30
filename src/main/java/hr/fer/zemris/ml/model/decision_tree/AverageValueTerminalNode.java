package hr.fer.zemris.ml.model.decision_tree;

public class AverageValueTerminalNode extends Node<Double> {

	private static final long serialVersionUID = 1L;

	private double value;

	public AverageValueTerminalNode(double value) {
		this.value = value;
	}

	@Override
	public Double predict(double[] features) {
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
