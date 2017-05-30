package hr.fer.zemris.ml.training;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Util {

	private static ThreadLocal<Random> randoms = ThreadLocal.withInitial(Random::new);

	public static <T> List<T> randomWithReplacement(List<T> samples, int size) {
		List<T> radnomSamples = new LinkedList<>();
		int bound = samples.size();
		for (int i = 0; i < size; i++) {
			radnomSamples.add(samples.get(randoms.get().nextInt(bound)));
		}
		return radnomSamples;
	}

	public static List<Integer> randomWithoutReplacement(int bound, int size) {
		if (size > bound) {
			throw new IllegalArgumentException();
		}

		List<Integer> list = new ArrayList<>();
		while (list.size() < size) {
			int n = randoms.get().nextInt(bound);
			if (!list.contains(n)) {
				list.add(n);
			}
		}
		return list;
	}
	
}
