package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.List;
import java.util.function.Predicate;

import hr.fer.zemris.ml.model.data.Sample;


public interface ISplitCriterion<T> {

	Predicate<double[]> getBestSplit(List<Sample<T>> samples, List<Integer> featureIndices);
}
