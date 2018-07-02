package com.axonactive.digidocs.utils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface CollectionUtils {
	static <T> Stream<T> nullSafeStream(List<T> list) {
		if(list == null) {
			return Stream.empty();
		}
		return list.stream().filter(Objects::nonNull);
	}
}
