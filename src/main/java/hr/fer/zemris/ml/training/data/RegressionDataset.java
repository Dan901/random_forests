package hr.fer.zemris.ml.training.data;

/**
 * Regression dataset with target values represented by a {@code Double}.
 *
 * @author Dan
 */
public class RegressionDataset extends Dataset<Double> {

	@Override
	protected Double parseTargetVariable(String target) {
		return Double.parseDouble(target);
	}

}
