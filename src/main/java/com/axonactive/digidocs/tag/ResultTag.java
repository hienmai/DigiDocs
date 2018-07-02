package com.axonactive.digidocs.tag;

import lombok.Builder;
import lombok.Getter;

public class ResultTag extends TagBOM {
	@Getter
	private int tagCount;

	@Builder
	public ResultTag(Integer id, String name, int count) {
		super(id, name);
		this.tagCount = count;
	}
}
