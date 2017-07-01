package hr.fer.zemris.ml.training.decision_tree;

import java.util.List;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;

/**
 * Interface for decision tree generators.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public interface ITreeGenerator<T> {

	/**
	 * Trains a decision tree on given samples.
	 * 
	 * @param samples training samples
	 * @return a trained tree
	 */
	DecisionTree<T> buildTree(List<Sample<T>> samples);
}
