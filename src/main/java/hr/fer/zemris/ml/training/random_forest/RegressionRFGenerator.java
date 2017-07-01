package hr.fer.zemris.ml.training.random_forest;

import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.model.random_forest.RegressionRandomForest;
import hr.fer.zemris.ml.training.data.RegressionDataset;
import hr.fer.zemris.ml.training.decision_tree.ITreeGenerator;

/**
 * Regression random forest generator.
 *
 * @see RFGenerator
 * @author Dan
 */
public class RegressionRFGenerator extends RFGenerator<Double> {

	/**
	 * Creates a new {@code RegressionRFGenerator}.
	 * 
	 * @param dataset training samples
	 * @param treeGenerator generator of a single decision tree
	 */
	public RegressionRFGenerator(RegressionDataset dataset, ITreeGenerator<Double> treeGenerator) {
		super(dataset, treeGenerator);
	}

	/**
	 * Starts the forest building process. Each tree is generated with the
	 * generator received in the constructor.
	 * <p>
	 * After every tree is generated,
	 * {@link IRFGeneratorObserver#regressionTreeConstructed} method is called
	 * on registered observers.
	 * 
	 * @param size number of trees to build
	 * @return trained random forest model
	 */
	public RegressionRandomForest buildForest(int size) {
		return new RegressionRandomForest(buildTrees(size), dataset.getNumOfFeatures());
	}

	@Override
	protected void notifyObservers(DecisionTree<Double> tree) {
		Set<Sample<Double>> testSet = new HashSet<>(dataset.getSamples());
		testSet.removeAll(tree.getTrainingSamples());
		double error = 0;
		for (Sample<Double> s : testSet) {
			double v = tree.predict(s.getFeatures());
			error += Math.pow(v - s.getTarget(), 2);
		}
		for (IRFGeneratorObserver o : observers) {
			o.regressionTreeConstructed(tree.getDepth(), error / dataset.getSize());
		}
	}
}
