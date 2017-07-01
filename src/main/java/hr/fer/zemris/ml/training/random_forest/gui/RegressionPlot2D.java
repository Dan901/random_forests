package hr.fer.zemris.ml.training.random_forest.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.List;
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
import hr.fer.zemris.ml.training.data.RegressionDataset;

/**
 * Plots the regression training samples if they have only 1 feature (single
 * variable function) on a 2D graph. Also function approximation is plotted
 * based on regressor trained on those samples.
 *
 * @author Dan
 */
public class RegressionPlot2D extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final double EXTRA_SPACING = 0.1;

	private RegressionDataset trainingDataset;
	private IPredictor<Double> regressor;

	public RegressionPlot2D(RegressionDataset trainingDataset, IPredictor<Double> regressor) {
		this.trainingDataset = Objects.requireNonNull(trainingDataset);
		this.regressor = Objects.requireNonNull(regressor);
		if (trainingDataset.getNumOfFeatures() != 1) {
			throw new IllegalArgumentException("Only dataset with 1 feature can be plotted.");
		}

		setLocation(100, 100);
		setSize(800, 800);
		setTitle("2D Regression Plot");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		draw();
	}

	private void draw() {
		List<Sample<Double>> samples = trainingDataset.getSamples();
		double xmin = samples.stream().mapToDouble(s -> s.getFeature(0)).min().getAsDouble();
		double xmax = samples.stream().mapToDouble(s -> s.getFeature(0)).max().getAsDouble();
		double ymin = samples.stream().mapToDouble(Sample::getTarget).min().getAsDouble();
		double ymax = samples.stream().mapToDouble(Sample::getTarget).max().getAsDouble();
		double xextra = (xmax - xmin) * EXTRA_SPACING;
		double yextra = (ymax - ymin) * EXTRA_SPACING;
		xmin -= xextra;
		xmax += xextra;
		ymin -= yextra;
		ymax += yextra;
		ValueAxis x = new NumberAxis("x");
		ValueAxis y = new NumberAxis("y");
		x.setRange(xmin, xmax);
		y.setRange(ymin, ymax);
		double xstep = (xmax - xmin) / (getWidth() - 60);

		XYPlot plot = new XYPlot();
		plot.setDomainAxis(0, x);
		plot.setRangeAxis(0, y);

		Shape pointShape = new Ellipse2D.Double(0, 0, 5, 5);
		Shape pointShape2 = new Rectangle(0, 0, 5, 5);

		XYSeries trainingSeries = generateTrainingSeries();
		plot.setDataset(0, new XYSeriesCollection(trainingSeries));
		XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(false, true);
		ren.setSeriesShape(0, pointShape);
		plot.setRenderer(0, ren);

		XYSeries series = generateSeries(xmin, xmax, xstep);
		plot.setDataset(1, new XYSeriesCollection(series));
		XYLineAndShapeRenderer ren2 = new XYLineAndShapeRenderer(true, false);
		ren.setSeriesShape(1, pointShape2);
		plot.setRenderer(1, ren2);

		JFreeChart chart = new JFreeChart(plot);
		ChartPanel panel = new ChartPanel(chart, true);
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
	}

	private XYSeries generateTrainingSeries() {
		XYSeries series = new XYSeries("training");
		for (Sample<Double> sample : trainingDataset.getSamples()) {
			series.add(sample.getFeature(0), sample.getTarget());
		}
		return series;
	}

	private XYSeries generateSeries(double xmin, double xmax, double xstep) {
		XYSeries series = new XYSeries("model");
		double[] point = new double[1];
		for (double x = xmin; x <= xmax; x += xstep) {
			point[0] = x;
			double y = regressor.predict(point);
			series.add(x, y);
		}
		return series;
	}
}
