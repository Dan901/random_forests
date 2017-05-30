package hr.fer.zemris.ml.training.random_forest.gui;

import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import hr.fer.zemris.ml.model.data.Sample;
import hr.fer.zemris.ml.training.data.Dataset;

public class DatasetTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private Dataset<?> dataset;

	public DatasetTableModel(Dataset<?> dataset) {
		this.dataset = Objects.requireNonNull(dataset);
	}

	@Override
	public int getRowCount() {
		return dataset.getSamples().size();
	}

	@Override
	public int getColumnCount() {
		return dataset.getNumOfFeatures() + 2;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return rowIndex + 1 + ".";
		}
		columnIndex--;
		Sample<?> sample = dataset.getSamples().get(rowIndex);
		if (columnIndex == dataset.getNumOfFeatures()) {
			return sample.getTarget();
		}
		return sample.getFeature(columnIndex);

	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Index";
		}
		column--;
		List<String> names = dataset.getVariableNames();
		if (names == null || names.size() <= column) {
			return super.getColumnName(column);
		}
		return names.get(column);
	}
}
