package com.axonactive.digidocs.file;

public interface Command {
	Command execute();
	CommandResult getCommandResult();
}
