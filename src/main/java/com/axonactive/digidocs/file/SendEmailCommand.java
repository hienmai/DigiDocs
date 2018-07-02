package com.axonactive.digidocs.file;

import java.util.Optional;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.email.EmailHandler;
import com.axonactive.digidocs.email.EmailInformation;
import com.axonactive.digidocs.email.EmailInformationFactory;
import com.axonactive.digidocs.email.FileBOM;
import com.axonactive.digidocs.email.ServerConfiguration;

import lombok.Getter;

public class SendEmailCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(SendEmailCommand.class);

	private Command previousCommand;
	@Getter
	private CommandResult commandResult;

	private ServerConfiguration serverConfiguration;

	public SendEmailCommand(Command previousCommand, ServerConfiguration serverConfiguration) {
		this.previousCommand = previousCommand;
		this.serverConfiguration = serverConfiguration;
	}

	@Override
	public Command execute() {
		Command fileCommandResult = previousCommand.execute();

		if (fileCommandResult.getCommandResult().getResult() != Result.STEP_SUCESS) {
			return fileCommandResult;
		}

		sendTheNotificationEmail(fileCommandResult.getCommandResult().getFileBOMBuilder());
		fileCommandResult.getCommandResult().setResult(Result.SUCCESS);
		return fileCommandResult;
	}

	private void sendTheNotificationEmail(FileBOM.FileBOMBuilder fileBOMBuilder) {
		LOGGER.debug("BEGIN TO SEND EMAIL");
		Optional<EmailInformation> emailInformationOptional = EmailInformationFactory
				.getEmailInformation(fileBOMBuilder.build());
		if (!emailInformationOptional.isPresent()) {
			return;
		}
		EmailInformation emailInformation = emailInformationOptional.get();
		EmailHandler.sendMail(emailInformation, serverConfiguration);
		LOGGER.debug("SENT EMAIL");
	}
}
