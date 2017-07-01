package hr.fer.zemris.ml.model;

/**
 * Interface for predictors, for example: classifiers or regressors. Contains
 * one method for target value prediction based on given features.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public interface IPredictor<T> {

	/**
	 * Predicts the target value based on given {@code array} of features.
	 * 
	 * @param features features to consider for prediction
	 * @return target value prediction
	 */
	T predict(double[] features);
}
