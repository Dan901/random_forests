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

public class CARTGenerator<T> implements ITreeGenerator<T> {

	private ISplitCriterion<T> splitCriterion;
	private ITerminalNodeFactory<T> factory;
	private int maxDepth;
	private int numOfFeatures;
	private int featuresToCheck;

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
