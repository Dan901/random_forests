package hr.fer.zemris.ml.training.random_forest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.model.random_forest.ClassificationRandomForest;
import hr.fer.zemris.ml.training.data.ClassificationDataset;
import hr.fer.zemris.ml.training.decision_tree.ITreeGenerator;

public class ClassificationRFGenerator extends RFGenerator<String> {

	private Set<String> classes;

	public ClassificationRFGenerator(ClassificationDataset dataset, ITreeGenerator<String> treeGenerator) {
		super(dataset, treeGenerator);
		classes = dataset.getAllClasses();
	}

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
