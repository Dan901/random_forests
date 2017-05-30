package hr.fer.zemris.ml.training.decision_tree;

import java.util.List;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;

public interface ITreeGenerator<T> {

	DecisionTree<T> buildTree(List<Sample<T>> samples);
}
