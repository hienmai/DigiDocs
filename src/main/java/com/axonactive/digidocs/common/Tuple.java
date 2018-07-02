package com.axonactive.digidocs.common;

import java.util.Collections;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Tuple<X, Y> {
	@SuppressWarnings("rawtypes")
	private static final Tuple EMPTY = new Tuple<>();
	
	@SuppressWarnings("unchecked")
	public static <W,Z> Tuple<W,Z> empty() {
		return (Tuple<W,Z>) Tuple.EMPTY;
	}
	
	private X x;
	private Y y;
}
