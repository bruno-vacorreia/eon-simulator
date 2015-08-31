package br.ufpe.simulator.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionsUtils {

	public static <T> T getLast(List<T> list) {
		T t = null;
		if (!list.isEmpty()) {
			t = list.get(list.size() - 1);
		}
		return t;
	}

	public static <T> T getFirst(List<T> list) {
		return list != null && !list.isEmpty() ? list.get(0) : null;
	}

	public static <T> boolean isNullOrEmpty(Collection<T> collection) {
		return collection == null || collection.isEmpty();
	}

	public static <T> List<T> subList(List<T> list, int size) {
		List<T> newList = null;
		if (list != null) {
			newList = new ArrayList<T>();
			for (int i = 0; i < size; i++) {
				newList.add(list.get(i));
			}
		}
		return newList;
	}
}