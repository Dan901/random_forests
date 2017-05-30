package hr.fer.zemris.ml.training.data;

public class RegressionDataset extends Dataset<Double> {

	@Override
	protected Double parseTargetVariable(String target) {
		return Double.parseDouble(target);
	}

}
