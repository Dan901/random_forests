package hr.fer.zemris.ml.training.random_forest.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.model.random_forest.ClassificationRandomForest;
import hr.fer.zemris.ml.model.random_forest.RandomForest;
import hr.fer.zemris.ml.model.random_forest.RegressionRandomForest;
import hr.fer.zemris.ml.training.data.ClassificationDataset;
import hr.fer.zemris.ml.training.data.Dataset;
import hr.fer.zemris.ml.training.data.RegressionDataset;
import hr.fer.zemris.ml.training.decision_tree.CARTGenerator;
import hr.fer.zemris.ml.training.decision_tree.ITerminalNodeFactory;
import hr.fer.zemris.ml.training.decision_tree.ITreeGenerator;
import hr.fer.zemris.ml.training.decision_tree.TerminalNodeFactory;
import hr.fer.zemris.ml.training.decision_tree.split.AbstractSplitCriterion;
import hr.fer.zemris.ml.training.decision_tree.split.EntropyReduction;
import hr.fer.zemris.ml.training.decision_tree.split.GiniIndexReduction;
import hr.fer.zemris.ml.training.decision_tree.split.SDReduction;
import hr.fer.zemris.ml.training.random_forest.ClassificationRFGenerator;
import hr.fer.zemris.ml.training.random_forest.IRFGeneratorObserver;
import hr.fer.zemris.ml.training.random_forest.RFGenerator;
import hr.fer.zemris.ml.training.random_forest.RegressionRFGenerator;

public class RandomForestsGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new RandomForestsGUI().setVisible(true));
	}

	private static enum Type {
		CLASSIFICATION, REGRESSION;
	}

	private Type type;
	private JTable table;
	private Dataset<?> dataset;
	private RandomForest<?> rfModel;

	private Action exitAction = new AbstractAction("Exit") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose();
		}
	};

	private Action loadDataAction = new AbstractAction("Load data") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(".");
			fc.setFileFilter(new FileNameExtensionFilter("CSV", "csv"));
			if (fc.showOpenDialog(RandomForestsGUI.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			Path file = fc.getSelectedFile().toPath();
			if (!Files.isReadable(file)) {
				showErrorMessage("File cannot be opened.");
				return;
			}
			loadData(file);
		}
	};

	private Action buildAction = new AbstractAction("Build forest") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JDialog dialog = new JDialog(RandomForestsGUI.this, "Build forest", true);
			dialog.setLayout(new BorderLayout());
			dialog.add(new BuildRandomForestPanel(), BorderLayout.CENTER);
			dialog.setSize(500, 600);
			dialog.setLocationRelativeTo(RandomForestsGUI.this);
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		}
	};

	private Action saveAction = new AbstractAction("Save") {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser(".");
			fc.setFileFilter(new FileNameExtensionFilter("Serial", "serial"));
			if (fc.showOpenDialog(RandomForestsGUI.this) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			Path file = fc.getSelectedFile().toPath();
			try {
				rfModel.saveToFile(file);
			} catch (IOException e1) {
				e1.printStackTrace();
				showErrorMessage("Unable to save model.");
			}
		}
	};

	public RandomForestsGUI() {
		setLocation(100, 100);
		setSize(800, 800);
		setTitle("Random Forests");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		initGUI();
	}

	private void initGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		type = chooseType();
		setLayout(new BorderLayout());
		createMenus();
		table = new JTable();
		JScrollPane scroll = new JScrollPane(table);
		add(scroll, BorderLayout.CENTER);
	}

	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		fileMenu.setMnemonic(KeyEvent.VK_F);

		fileMenu.add(loadDataAction);
		loadDataAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control N"));
		loadDataAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_L);

		fileMenu.addSeparator();
		fileMenu.add(exitAction);
		exitAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("alt F4"));
		exitAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_X);

		JMenu runMenu = new JMenu("Run");
		menuBar.add(runMenu);
		runMenu.setMnemonic(KeyEvent.VK_R);

		runMenu.add(buildAction);
		buildAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke("control B"));
		buildAction.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_B);
		buildAction.setEnabled(false);
	}

	private void loadData(Path file) {
		dataset = type == Type.CLASSIFICATION ? new ClassificationDataset() : new RegressionDataset();
		try {
			dataset.loadFromCSV(file, true);
		} catch (Exception e) {
			showErrorMessage("Error during data loading: " + e.getMessage());
			return;
		}

		DatasetTableModel tableModel = new DatasetTableModel(dataset);
		table.setModel(tableModel);
		buildAction.setEnabled(true);
	}

	private Type chooseType() {
		Object[] options = new Object[] { "Classification", "Function approximation" };
		int option = JOptionPane.showOptionDialog(this, "Choose the type of random forest:", "Random Forest Type",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, null);
		if (option == JOptionPane.CLOSED_OPTION) {
			System.exit(0);
		}
		return option == 0 ? Type.CLASSIFICATION : Type.REGRESSION;
	}

	private void showErrorMessage(String msg) {
		JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private class BuildRandomForestPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		private JTextField numOfTrees;
		private JTextField maxDepth;
		private JTextField minSamples;
		private JSlider featuresToCheck;
		private JComboBox<String> terminalNode;
		private JComboBox<String> splitCriterion;
		private JButton runButton;
		private JProgressBar bar;
		private JTextArea output;

		private Action run = new AbstractAction("Run") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveAction.setEnabled(false);
					rfModel = null;
					run();
				} catch (Exception ex) {
					showErrorMessage("Error occurred: " + ex.getMessage());
				}
			}
		};

		public BuildRandomForestPanel() {
			initGUI();
		}

		private void initGUI() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JPanel parametersParent = new JPanel(new BorderLayout());
			add(parametersParent, BorderLayout.PAGE_START);
			JPanel parameters = new JPanel(new GridLayout(0, 2, 3, 2));
			parametersParent.add(parameters, BorderLayout.PAGE_START);

			NumberFormat intFormat = NumberFormat.getIntegerInstance();
			intFormat.setGroupingUsed(false);
			NumberFormatter intNF = new NumberFormatter(intFormat);
			intNF.setAllowsInvalid(false);

			numOfTrees = new JFormattedTextField(intNF);
			numOfTrees.setText(Integer.toString(100));
			JLabel numOfTreesLabel = new JLabel("Number of trees: ");
			numOfTreesLabel.setHorizontalAlignment(JLabel.RIGHT);
			parameters.add(numOfTreesLabel);
			parameters.add(numOfTrees);

			maxDepth = new JFormattedTextField(intNF);
			maxDepth.setText(Integer.toString(10));
			JLabel maxDepthLabel = new JLabel("Max tree depth: ");
			maxDepthLabel.setHorizontalAlignment(JLabel.RIGHT);
			parameters.add(maxDepthLabel);
			parameters.add(maxDepth);

			minSamples = new JFormattedTextField(intNF);
			minSamples.setText(Integer.toString(1));
			JLabel minSamplesLabel = new JLabel("Minimum samples per node: ");
			minSamplesLabel.setHorizontalAlignment(JLabel.RIGHT);
			parameters.add(minSamplesLabel);
			parameters.add(minSamples);

			JPanel featuresPanel = new JPanel(new GridLayout(1, 2));
			parametersParent.add(featuresPanel, BorderLayout.PAGE_END);
			int features = dataset.getNumOfFeatures();
			featuresToCheck = new JSlider(SwingConstants.HORIZONTAL, 1, features, (int) Math.sqrt(features));
			featuresToCheck.setPaintLabels(true);
			featuresToCheck.setPaintTicks(true);
			featuresToCheck.setMinorTickSpacing(1);
			featuresToCheck.setMajorTickSpacing(Math.min(features - 1, 1));
			JLabel featuresToCheckLabel = new JLabel("Features to check at each node: ");
			featuresToCheckLabel.setHorizontalAlignment(JLabel.RIGHT);
			featuresPanel.add(featuresToCheckLabel);
			if (features > 1) {
				featuresPanel.add(featuresToCheck);
			} else {
				featuresPanel.add(new JLabel("1"));
			}

			if (type == Type.CLASSIFICATION) {
				terminalNode = new JComboBox<>(new Vector<>(classificationNodes.keySet()));
				splitCriterion = new JComboBox<>(new Vector<>(classificationSplits.keySet()));
			} else {
				terminalNode = new JComboBox<>(new Vector<>(regressionNodes.keySet()));
				splitCriterion = new JComboBox<>(new Vector<>(regressionSplits.keySet()));
			}

			JLabel terminalNodeLabel = new JLabel("Select terminal node type: ");
			terminalNodeLabel.setHorizontalAlignment(JLabel.RIGHT);
			parameters.add(terminalNodeLabel);
			parameters.add(terminalNode);

			JLabel splitCriterionLabel = new JLabel("Select split criterion: ");
			splitCriterionLabel.setHorizontalAlignment(JLabel.RIGHT);
			parameters.add(splitCriterionLabel);
			parameters.add(splitCriterion);

			JPanel progressPanel = new JPanel(new BorderLayout());
			progressPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
			add(progressPanel, BorderLayout.CENTER);

			JPanel topPanel = new JPanel();
			progressPanel.add(topPanel, BorderLayout.PAGE_START);

			runButton = new JButton(run);
			topPanel.add(runButton);

			bar = new JProgressBar(0, 1);
			bar.setStringPainted(true);
			topPanel.add(bar);

			saveAction.setEnabled(false);
			topPanel.add(new JButton(saveAction));

			output = new JTextArea();
			output.setFont(new Font("Dialog", Font.PLAIN, 12));
			output.setEditable(false);
			progressPanel.add(new JScrollPane(output), BorderLayout.CENTER);
		}

		private void run() {
			int ms = Integer.parseInt(minSamples.getText());
			int md = Integer.parseInt(maxDepth.getText());
			int ftc = featuresToCheck.getValue();
			int size = Integer.parseInt(numOfTrees.getText());

			RFGenerator<?> rfg;
			SwingWorker<?, ?> task;

			if (type == Type.CLASSIFICATION) {
				ClassificationDataset cd = (ClassificationDataset) dataset;
				//@formatter:off
				ITreeGenerator<String> treeGenerator = new CARTGenerator<>(
						classificationSplits.get(splitCriterion.getSelectedItem()).creatSplitCriterion(cd, ms),
						classificationNodes.get(terminalNode.getSelectedItem()),
						md,
						cd.getNumOfFeatures(),
						ftc
				);
				//@formatter:on
				ClassificationRFGenerator crfg = new ClassificationRFGenerator(cd, treeGenerator);
				ClassificationTask ct = new ClassificationTask(crfg, size);
				crfg.addObserver(ct);
				rfg = crfg;
				task = ct;
			} else {
				RegressionDataset rd = (RegressionDataset) dataset;
				//@formatter:off
				ITreeGenerator<Double> treeGenerator = new CARTGenerator<>(
						regressionSplits.get(splitCriterion.getSelectedItem()).creatSplitCriterion(rd, ms),
						regressionNodes.get(terminalNode.getSelectedItem()),
						md,
						rd.getNumOfFeatures(),
						ftc
				);
				//@formatter:on
				RegressionRFGenerator rrfg = new RegressionRFGenerator(rd, treeGenerator);
				RegressionTask rt = new RegressionTask(rrfg, size);
				rrfg.addObserver(rt);
				rfg = rrfg;
				task = rt;
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			bar.setMaximum(size);
			output.setText("");
			task.execute();

			run.setEnabled(false);
			runButton.setAction(new AbstractAction("Cancel") {
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					rfg.stop();
					task.cancel(true);
				}
			});
		}

		private class ClassificationTask extends SwingWorker<ClassificationRandomForest, String>
				implements IRFGeneratorObserver {

			private ClassificationRFGenerator crfg;
			private int size;
			private int constructed;

			public ClassificationTask(ClassificationRFGenerator crfg, int size) {
				this.crfg = crfg;
				this.size = size;
			}

			@Override
			protected ClassificationRandomForest doInBackground() throws Exception {
				return crfg.buildForest(size);
			}

			@Override
			protected void process(List<String> chunks) {
				for (String str : chunks) {
					bar.setValue(constructed);
					output.append(str);
				}
			}

			@Override
			protected void done() {
				if (isCancelled()) {
					runDone(true);
					return;
				}

				ClassificationRandomForest crf;
				while (true) {
					try {
						crf = get();
						break;
					} catch (InterruptedException ign) {
					} catch (ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
				rfModel = crf;

				ClassificationDataset cd = (ClassificationDataset) dataset;
				int n = 0;
				for (Sample<String> s : cd.getSamples()) {
					String c = crf.predict(s.getFeatures());
					if (c.equals(s.getTarget())) {
						n++;
					}
				}
				output.append(String.format("Random forest test on training data: %d/%d", n, cd.getSize()));

				if (dataset.getNumOfFeatures() == 2) {
					JFrame plot2D = new ClassificationPlot2D((ClassificationDataset) dataset, crf);
					plot2D.setLocation(500, 150);
					plot2D.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
					plot2D.setVisible(true);
				}

				runDone(false);
			}

			@Override
			public void classificationTreeConstructed(int depth, int oobCorrect, int oobSize) {
				constructed++;
				publish(String.format("Constructed tree %d, depth: %d, OOB test: %d/%d%n", constructed, depth,
						oobCorrect, oobSize));
			}

			@Override
			public void regressionTreeConstructed(int depth, double mse) {
			}
		}

		private class RegressionTask extends SwingWorker<RegressionRandomForest, String>
				implements IRFGeneratorObserver {

			private RegressionRFGenerator rrfg;
			private int size;
			private int constructed;

			public RegressionTask(RegressionRFGenerator rrfg, int size) {
				this.rrfg = rrfg;
				this.size = size;
			}

			@Override
			protected RegressionRandomForest doInBackground() throws Exception {
				return rrfg.buildForest(size);
			}

			@Override
			protected void process(List<String> chunks) {
				for (String str : chunks) {
					bar.setValue(constructed);
					output.append(str);
				}
			}

			@Override
			protected void done() {
				if (isCancelled()) {
					runDone(true);
					return;
				}

				RegressionRandomForest rrf;
				while (true) {
					try {
						rrf = get();
						break;
					} catch (InterruptedException ign) {
					} catch (ExecutionException e) {
						throw new RuntimeException(e);
					}
				}
				rfModel = rrf;

				RegressionDataset rd = (RegressionDataset) dataset;
				double error = 0;
				for (Sample<Double> s : rd.getSamples()) {
					double v = rrf.predict(s.getFeatures());
					error += Math.pow(v - s.getTarget(), 2);
				}
				output.append(String.format("Random forest test on training data MSE: %f", error / rd.getSize()));

				if (dataset.getNumOfFeatures() == 1) {
					JFrame plot2D = new RegressionPlot2D((RegressionDataset) dataset, rrf);
					plot2D.setLocation(500, 150);
					plot2D.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
					plot2D.setVisible(true);
				}

				runDone(false);
			}

			@Override
			public void classificationTreeConstructed(int depth, int oobCorrect, int oobSize) {
			}

			@Override
			public void regressionTreeConstructed(int depth, double mse) {
				constructed++;
				publish(String.format("Constructed tree %d, depth: %d, OOB test MSE: %f%n", constructed, depth, mse));
			}
		}

		private void runDone(boolean cancelled) {
			run.setEnabled(true);
			runButton.setAction(run);
			bar.setValue(0);
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			if (cancelled) {
				JOptionPane.showMessageDialog(BuildRandomForestPanel.this, "Cancelled");
			} else {
				JOptionPane.showMessageDialog(BuildRandomForestPanel.this, "Finished");
				saveAction.setEnabled(true);
			}
		}
	}

	private static interface ISplitCriterionFactory<T> {

		AbstractSplitCriterion<T> creatSplitCriterion(Dataset<T> dataset, int minSamplesPerNode);
	}

	private static final Map<String, ITerminalNodeFactory<String>> classificationNodes = new HashMap<>();
	private static final Map<String, ITerminalNodeFactory<Double>> regressionNodes = new HashMap<>();
	private static final Map<String, ISplitCriterionFactory<String>> classificationSplits = new HashMap<>();
	private static final Map<String, ISplitCriterionFactory<Double>> regressionSplits = new HashMap<>();
	static {
		classificationNodes.put("Classification node", TerminalNodeFactory.classificationNodeFactory);
		regressionNodes.put("Linear model node", TerminalNodeFactory.linearModelNodeFactory);
		regressionNodes.put("Average value node", TerminalNodeFactory.averageValueNodeFactory);
		classificationSplits.put("Gini index reduction", GiniIndexReduction::new);
		classificationSplits.put("Entropy reduction", EntropyReduction::new);
		regressionSplits.put("Standard deviation reduction", SDReduction::new);
	}
}
