package hr.fer.zemris.ml.model.decision_tree;

import java.io.Serializable;

import hr.fer.zemris.ml.model.IPredictor;

public abstract class Node<T> implements Serializable, IPredictor<T> {

	private static final long serialVersionUID = 1L;

	abstract public int getDepth();
}
