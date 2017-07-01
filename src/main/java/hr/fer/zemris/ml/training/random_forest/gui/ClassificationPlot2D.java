package hr.fer.zemris.ml.training.random_forest.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import hr.fer.zemris.ml.model.IPredictor;
import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.training.data.ClassificationDataset;

/**
 * Plots the classification training samples if they have only 2 features on a
 * 2D graph. Also surrounding area is colored based of predictions from a
 * classifier trained on those samples.
 *
 * @author Dan
 */
public class ClassificationPlot2D extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final double EXTRA_SPACING = 0.1;

	private ClassificationDataset trainingDataset;
	private IPredictor<String> classifier;

	public ClassificationPlot2D(ClassificationDataset trainingDataset, IPredictor<String> classifier) {
		this.trainingDataset = Objects.requireNonNull(trainingDataset);
		this.classifier = Objects.requireNonNull(classifier);
		if (trainingDataset.getNumOfFeatures() != 2) {
			throw new IllegalArgumentException("Only 2D dataset can be plotted.");
		}

		setLocation(100, 100);
		setSize(800, 800);
		setTitle("2D Classification Plot");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		draw();
	}

	private void draw() {
		List<Sample<String>> samples = trainingDataset.getSamples();
		double xmin = samples.stream().mapToDouble(s -> s.getFeature(0)).min().getAsDouble();
		double xmax = samples.stream().mapToDouble(s -> s.getFeature(0)).max().getAsDouble();
		double ymin = samples.stream().mapToDouble(s -> s.getFeature(1)).min().getAsDouble();
		double ymax = samples.stream().mapToDouble(s -> s.getFeature(1)).max().getAsDouble();
		double xextra = (xmax - xmin) * EXTRA_SPACING;
		double yextra = (ymax - ymin) * EXTRA_SPACING;
		xmin -= xextra;
		xmax += xextra;
		ymin -= yextra;
		ymax += yextra;
		ValueAxis x = new NumberAxis("x1");
		ValueAxis y = new NumberAxis("x2");
		x.setRange(xmin, xmax);
		y.setRange(ymin, ymax);

		double xstep = (xmax - xmin) / (getWidth() - 60) * 3;
		double ystep = (ymax - ymin) / (getHeight() - 80) * 3;

		XYPlot plot = new XYPlot();
		plot.setDomainAxis(0, x);
		plot.setRangeAxis(0, y);

		Shape pointShape = new Ellipse2D.Double(0, 0, 5, 5);
		Shape pointShape2 = new Rectangle(0, 0, 5, 5);

		Collection<XYSeries> trainingSeries = generateTrainingSeriesByClass();
		int i = 0;
		for (XYSeries s : trainingSeries) {
			plot.setDataset(i, new XYSeriesCollection(s));
			XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(false, true);
			ren.setSeriesShape(0, pointShape);
			plot.setRenderer(i, ren);
			i++;
		}

		Collection<XYSeries> series = generateSeriesByClass(xmin, xmax, xstep, ymin, ymax, ystep);
		for (XYSeries s : series) {
			plot.setDataset(i, new XYSeriesCollection(s));
			XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(false, true);
			ren.setSeriesShape(0, pointShape2);
			plot.setRenderer(i, ren);
			i++;
		}

		JFreeChart chart = new JFreeChart(plot);
		ChartPanel panel = new ChartPanel(chart, true);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
	}

	private Collection<XYSeries> generateSeriesByClass(double xmin, double xmax, double xstep, double ymin, double ymax,
			double ystep) {
		Map<String, XYSeries> seriesMap = new HashMap<>();
		double[] point = new double[2];
		for (double x = xmin; x <= xmax; x += xstep) {
			point[0] = x;
			for (double y = ymin; y <= ymax; y += ystep) {
				point[1] = y;
				String target = classifier.predict(point);
				if (!seriesMap.containsKey(target)) {
					seriesMap.put(target, new XYSeries(target));
				}
				seriesMap.get(target).add(x, y);
			}
		}
		return seriesMap.values();
	}

	private Collection<XYSeries> generateTrainingSeriesByClass() {
		Map<String, XYSeries> seriesMap = new HashMap<>();
		for (Sample<String> sample : trainingDataset.getSamples()) {
			String target = sample.getTarget();
			if (!seriesMap.containsKey(target)) {
				seriesMap.put(target, new XYSeries(target + "-training"));
			}
			seriesMap.get(target).add(sample.getFeature(0), sample.getFeature(1));
		}
		return seriesMap.values();
	}
}
