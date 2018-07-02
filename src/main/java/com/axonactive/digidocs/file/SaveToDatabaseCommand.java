package com.axonactive.digidocs.file;

import java.util.Optional;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.email.FileEntity;
import com.axonactive.digidocs.email.FileService;
import com.axonactive.digidocs.utils.CDIUtils;

import lombok.Getter;

public class SaveToDatabaseCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(SaveToDatabaseCommand.class);

	private Command previousCommand;
	@Getter
	private CommandResult commandResult;
	private FileService fileService;

	public SaveToDatabaseCommand(Command previousCommand) {
		this.previousCommand = previousCommand;
		fileService = CDIUtils.getBean(FileService.class);
	}

	@Override
	public Command execute() {
		Command commandResult = previousCommand.execute();

		if (commandResult.getCommandResult().getResult() != Result.STEP_SUCESS) {
			return commandResult;
		}

		if (!saveToDatabase(commandResult.getCommandResult().getFileBOMBuilder())) {
			commandResult.getCommandResult().setResult(Result.FAIL_DATABASE_SAVING);
		}

		return commandResult;
	}

	private boolean saveToDatabase(FileBOM.FileBOMBuilder fileBOMBuilder) {
		Optional<FileEntity> fileEntityOptional = fileService.toEntity(fileBOMBuilder.build());
		if (!fileEntityOptional.isPresent()) {
			return false;
		}
		FileEntity fileEntity = fileService.save(fileEntityOptional.get());
		fileBOMBuilder.id(fileEntity.getId());
		LOGGER.debug("Save in database successfully");
		return true;
	}
}
