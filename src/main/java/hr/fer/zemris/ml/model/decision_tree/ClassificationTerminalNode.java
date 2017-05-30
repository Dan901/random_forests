package hr.fer.zemris.ml.model.decision_tree;

import java.util.Objects;

public class ClassificationTerminalNode extends Node<String> {

	private static final long serialVersionUID = 1L;

	private String target;

	public ClassificationTerminalNode(String target) {
		this.target = Objects.requireNonNull(target);
	}

	@Override
	public String predict(double[] features) {
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
