package hr.fer.zemris.ml.training.random_forest;

/**
 * Interface for observers of random forest building.
 *
 * @author Dan
 */
public interface IRFGeneratorObserver {

	/**
	 * Called when a classification decision tree is constructed.
	 * 
	 * @param depth depth of the tree
	 * @param oobCorrect correctly classified samples that were not used for
	 *        training
	 * @param oobSize number of samples that were not used for training
	 */
	void classificationTreeConstructed(int depth, int oobCorrect, int oobSize);

	/**
	 * Called when a regression decision tree is constructed.
	 * 
	 * @param depth depth of the tree
	 * @param mse mean squared error of predictions on samples that were not
	 *        used for training
	 */
	void regressionTreeConstructed(int depth, double mse);
}
