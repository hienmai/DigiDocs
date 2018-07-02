package com.axonactive.digidocs.file;

import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.email.FileBOM.FileBOMBuilder;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommandResult {
	private FileBOMBuilder fileBOMBuilder = FileBOM.builder();
	private Result result;
}
