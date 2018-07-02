package com.axonactive.digidocs.utils;

import java.util.Optional;
import java.util.stream.Stream;

public interface OptionalUtils {
	static <T> Stream<T> flatMap(Stream<Optional<T>> stream) {
		return stream.filter(Optional::isPresent)
						.map(Optional::get);
	}
}
