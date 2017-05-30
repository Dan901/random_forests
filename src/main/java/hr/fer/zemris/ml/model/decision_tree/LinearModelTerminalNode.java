package hr.fer.zemris.ml.model.decision_tree;

import java.util.Objects;

public class LinearModelTerminalNode extends Node<Double> {

	private static final long serialVersionUID = 1L;

	private double[] parameters;

	public LinearModelTerminalNode(double[] parameters) {
		this.parameters = Objects.requireNonNull(parameters);
	}

	@Override
	public Double getTargetValue(double[] features) {
		int n = parameters.length - 1;
		if (n != features.length) {
			throw new IllegalArgumentException("This node is built to estimate target value from:" + n + " features.");
		}

		double value = parameters[0];
		for (int i = 0; i < n; i++) {
			value += features[i] * parameters[i + 1];
		}
		return value;
	}

	@Override
	public int getDepth() {
		return 0;
	}

}
