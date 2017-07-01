package hr.fer.zemris.ml.training.random_forest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.model.random_forest.ClassificationRandomForest;
import hr.fer.zemris.ml.training.data.ClassificationDataset;
import hr.fer.zemris.ml.training.decision_tree.ITreeGenerator;

/**
 * Classification random forest generator.
 *
 * @see RFGenerator
 * @author Dan
 */
public class ClassificationRFGenerator extends RFGenerator<String> {

	private Set<String> classes;

	/**
	 * Creates a new {@code ClassificationRFGenerator}.
	 * 
	 * @param dataset training samples
	 * @param treeGenerator generator of a single decision tree
	 */
	public ClassificationRFGenerator(ClassificationDataset dataset, ITreeGenerator<String> treeGenerator) {
		super(dataset, treeGenerator);
		classes = dataset.getAllClasses();
	}

	/**
	 * Starts the forest building process. Each tree is generated with the
	 * generator received in the constructor.
	 * <p>
	 * After every tree is generated,
	 * {@link IRFGeneratorObserver#classificationTreeConstructed} method is
	 * called on registered observers.
	 * 
	 * @param size number of trees to build
	 * @return trained random forest model
	 */
	public ClassificationRandomForest buildForest(int size) {
		return new ClassificationRandomForest(buildTrees(size), dataset.getNumOfFeatures(), new ArrayList<>(classes));
	}

	@Override
	protected void notifyObservers(DecisionTree<String> tree) {
		Set<Sample<String>> testSet = new HashSet<>(dataset.getSamples());
		testSet.removeAll(tree.getTrainingSamples());
		int n = 0;
		for (Sample<String> s : testSet) {
			String c = tree.predict(s.getFeatures());
			if (c.equals(s.getTarget())) {
				n++;
			}
		}
		for (IRFGeneratorObserver observer : observers) {
			observer.classificationTreeConstructed(tree.getDepth(), n, testSet.size());
		}
	}
}
