package hr.fer.zemris.ml.model.data;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Training sample for supervised learning methods. Contains a
 * {@code double array} of features and a target value.
 *
 * @author Dan
 * @param <T> Type of the target value, usually {@code String} for
 *        classification and {@code Double} for function approximation tasks.
 */
public class Sample<T> {

	private double[] features;
	private T target;

	public Sample(double[] features, T target) {
		this.features = Objects.requireNonNull(features);
		this.target = target;
	}

	public int getNumOfFeatures() {
		return features.length;
	}

	public double getFeature(int index) {
		return features[index];
	}

	public double[] getFeatures() {
		return features;
	}

	public T getTarget() {
		return target;
	}

	public void setTarget(T target) {
		this.target = target;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(features);
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sample other = (Sample) obj;
		if (!Arrays.equals(features, other.features))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringJoiner sj = new StringJoiner(",");
		for (double d : features) {
			sj.add(Double.toString(d));
		}
		sj.add(target.toString());
		return sj.toString();
	}
}
