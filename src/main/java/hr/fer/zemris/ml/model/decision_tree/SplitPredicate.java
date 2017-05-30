package hr.fer.zemris.ml.model.decision_tree;

import java.io.Serializable;
import java.util.function.Predicate;

public class SplitPredicate implements Predicate<double[]>, Serializable {

	private static final long serialVersionUID = 1L;
	
	public int featureIndex;
	public double threshold;

	public SplitPredicate() {
	}

	public SplitPredicate(int featureIndex, double threshold) {
		this.featureIndex = featureIndex;
		this.threshold = threshold;
	}

	@Override
	public boolean test(double[] t) {
		return t[featureIndex] < threshold;
	}

	@Override
	public String toString() {
		return "Index: " + featureIndex + " Threshold: " + threshold;
	}
}
