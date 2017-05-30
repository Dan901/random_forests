package hr.fer.zemris.ml.model;

public interface IPredictor<T> {

	T predict(double[] features);
}
