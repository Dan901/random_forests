package hr.fer.zemris.ml.training.decision_tree;

import java.util.List;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.Node;

/**
 * Interface for factories that create a terminal node in a decision tree.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public interface ITerminalNodeFactory<T> {

	/**
	 * Creates a terminal node with given samples.
	 * 
	 * @param samples subset of training samples from which the node should be
	 *        created
	 * @return generated node
	 */
	Node<T> createTerminal(List<Sample<T>> samples);
}
