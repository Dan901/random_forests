package hr.fer.zemris.ml.training.random_forest;

public interface IRFGeneratorObserver {

	void classificationTreeConstructed(int depth, int oobCorrect, int oobSize);
	
	void regressionTreeConstructed(int depth, double mse);
}
