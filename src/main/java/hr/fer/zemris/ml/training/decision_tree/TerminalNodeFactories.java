package hr.fer.zemris.ml.training.decision_tree;

import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.AverageValueTerminalNode;
import hr.fer.zemris.ml.model.decision_tree.ClassificationTerminalNode;
import hr.fer.zemris.ml.model.decision_tree.LinearModelTerminalNode;
import hr.fer.zemris.ml.model.decision_tree.Node;

public class TerminalNodeFactories {

	public static final ITerminalNodeFactory<String> classificationNodeFactory = new ITerminalNodeFactory<String>() {
		@Override
		public Node<String> createTerminal(List<Sample<String>> samples) {
			String target = samples.stream().map(Sample::getTarget)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
					.max(Comparator.comparing(Entry::getValue)).get().getKey();
			return new ClassificationTerminalNode(target);
		}
	};

	public static final ITerminalNodeFactory<Double> averageValueNodeFactory = new ITerminalNodeFactory<Double>() {
		@Override
		public Node<Double> createTerminal(List<Sample<Double>> samples) {
			double value = samples.stream().mapToDouble(Sample::getTarget).average().getAsDouble();
			return new AverageValueTerminalNode(value);
		}
	};

	public static final ITerminalNodeFactory<Double> linearModelNodeFactory = new ITerminalNodeFactory<Double>() {
		@Override
		public Node<Double> createTerminal(List<Sample<Double>> samples) {
			double[] y = samples.stream().mapToDouble(Sample::getTarget).toArray();
			double[][] x = samples.stream().map(Sample::getFeatures).toArray(double[][]::new);
			OLSMultipleLinearRegression regression = new OLSMultipleLinearRegression();
			regression.newSampleData(y, x);
			double[] parameters = regression.estimateRegressionParameters();
			return new LinearModelTerminalNode(parameters);
		}
	};
}
