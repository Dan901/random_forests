package hr.fer.zemris.ml.training.decision_tree.split;

import java.util.List;
import java.util.function.Predicate;

import hr.fer.zemris.ml.model.data.Sample;

/**
 * Interface for evaluating the best split at the current node during decision
 * tree training.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public interface ISplitCriterion<T> {

	/**
	 * Returns the best split of given samples considering only features whose
	 * indices are also given.
	 * 
	 * @param samples samples to split into 2 sets
	 * @param featureIndices indices of features to consider for a split
	 * @return {@code Predicate} which splits given samples into 2 sets based on
	 *         the evaluation of the best split
	 */
	Predicate<double[]> getBestSplit(List<Sample<T>> samples, List<Integer> featureIndices);
}
