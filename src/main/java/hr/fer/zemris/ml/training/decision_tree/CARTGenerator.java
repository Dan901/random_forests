package hr.fer.zemris.ml.training.decision_tree;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.BinaryNode;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.model.decision_tree.Node;
import hr.fer.zemris.ml.training.Util;
import hr.fer.zemris.ml.training.decision_tree.split.ISplitCriterion;

/**
 * Decision tree generator based on CART algorithm. Supports only features whose
 * values are continuous ({@code double}).
 * <p>
 * User can specify which split evaluation criterion, which terminal node
 * factory and which parameters will be used.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public class CARTGenerator<T> implements ITreeGenerator<T> {

	private ISplitCriterion<T> splitCriterion;
	private ITerminalNodeFactory<T> factory;
	private int maxDepth;
	private int numOfFeatures;
	private int featuresToCheck;

	/**
	 * Creates a new {@code CARTGenerator} with given arguments.
	 * 
	 * @param splitCriterion criterion to evaluate the best possible split at
	 *        the current node
	 * @param factory terminal node factory
	 * @param maxDepth max tree depth
	 * @param numOfFeatures number of features all training samples have
	 * @param featuresToCheck number of features to randomly choose at each node
	 *        that will be considered for splitting the current samples
	 */
	public CARTGenerator(ISplitCriterion<T> splitCriterion, ITerminalNodeFactory<T> factory, int maxDepth,
			int numOfFeatures, int featuresToCheck) {
		if (maxDepth < 1) {
			throw new IllegalArgumentException("Max depth cannot be less than 1.");
		}
		this.maxDepth = maxDepth;

		if (numOfFeatures < 1) {
			throw new IllegalArgumentException("Number of feature cannot be less than 1.");
		}
		this.numOfFeatures = numOfFeatures;

		if (featuresToCheck < 1 || featuresToCheck > numOfFeatures) {
			throw new IllegalArgumentException("Number of features to check has to be between 1 and " + numOfFeatures);
		}
		this.featuresToCheck = featuresToCheck;

		this.splitCriterion = Objects.requireNonNull(splitCriterion);
		this.factory = Objects.requireNonNull(factory);
	}

	@Override
	public DecisionTree<T> buildTree(List<Sample<T>> samples) {
		Node<T> root = generateNode(samples, 0);
		return new DecisionTree<>(root, samples);
	}

	private Node<T> generateNode(List<Sample<T>> samples, int depth) {
		if (depth >= maxDepth) {
			return factory.createTerminal(samples);
		}

		List<Integer> features = Util.randomWithoutReplacement(numOfFeatures, featuresToCheck);
		Predicate<double[]> splitPredicate = splitCriterion.getBestSplit(samples, features);
		Map<Boolean, List<Sample<T>>> split = samples.stream()
				.collect(Collectors.partitioningBy(s -> splitPredicate.test(s.getFeatures())));

		if (split.get(true).isEmpty() || split.get(false).isEmpty()) {
			return factory.createTerminal(samples);
		}

		try {
			Node<T> left = generateNode(split.get(true), depth + 1);
			Node<T> right = generateNode(split.get(false), depth + 1);
			BinaryNode<T> node = new BinaryNode<>(left, right, splitPredicate);
			return node;
		} catch (Exception e) {
			return factory.createTerminal(samples);
		}
	}
}
