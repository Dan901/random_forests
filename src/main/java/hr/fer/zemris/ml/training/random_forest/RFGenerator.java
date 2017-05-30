package hr.fer.zemris.ml.training.random_forest;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.decision_tree.DecisionTree;
import hr.fer.zemris.ml.training.Util;
import hr.fer.zemris.ml.training.data.Dataset;
import hr.fer.zemris.ml.training.decision_tree.ITreeGenerator;

public abstract class RFGenerator<T> {

	protected List<IRFGeneratorObserver> observers;
	protected Dataset<T> dataset;
	private ITreeGenerator<T> treeGenerator;
	private ExecutorService pool;

	public RFGenerator(Dataset<T> dataset, ITreeGenerator<T> treeGenerator) {
		this.dataset = Objects.requireNonNull(dataset);
		this.treeGenerator = Objects.requireNonNull(treeGenerator);
		observers = new CopyOnWriteArrayList<>();
	}

	public void addObserver(IRFGeneratorObserver observer) {
		observers.add(Objects.requireNonNull(observer));
	}

	public void removeObserver(IRFGeneratorObserver observer) {
		observers.remove(observer);
	}

	public void stop() {
		pool.shutdownNow();
	}

	protected List<DecisionTree<T>> buildTrees(int size) {
		if (size < 1) {
			throw new IllegalArgumentException("Forest cannot have less than 1 tree.");
		}

		List<DecisionTree<T>> trees = new LinkedList<>();
		pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		List<Future<DecisionTree<T>>> results = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			results.add(pool.submit(() -> {
				List<Sample<T>> samples = dataset.getSamples();
				List<Sample<T>> randomSamples = Util.randomWithReplacement(samples, samples.size());
				return treeGenerator.buildTree(randomSamples);
			}));
		}
		for (Future<DecisionTree<T>> result : results) {
			while (true) {
				try {
					DecisionTree<T> tree = result.get();
					notifyObservers(tree);
					trees.add(tree);
					break;
				} catch (InterruptedException ignorable) {
				} catch (ExecutionException e) {
					throw new RuntimeException("Exception during building trees.", e);
				}
			}
		}
		pool.shutdown();
		return trees;
	}

	abstract protected void notifyObservers(DecisionTree<T> tree);
}
