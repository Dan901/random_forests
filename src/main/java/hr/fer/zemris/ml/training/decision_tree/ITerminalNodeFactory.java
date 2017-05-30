package hr.fer.zemris.ml.training.decision_tree;

import java.util.List;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.Node;


public interface ITerminalNodeFactory<T> {

	Node<T> createTerminal(List<Sample<T>> samples);
}
