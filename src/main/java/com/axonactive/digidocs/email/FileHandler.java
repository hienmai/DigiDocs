package com.axonactive.digidocs.email;

import java.io.File;

import com.axonactive.digidocs.file.Command;
import com.axonactive.digidocs.file.MoveFileCommand;
import com.axonactive.digidocs.file.Result;
import com.axonactive.digidocs.file.SaveToDatabaseCommand;
import com.axonactive.digidocs.file.ScanCommand;
import com.axonactive.digidocs.file.SendEmailCommand;

public class FileHandler {
	private File scannedFile;

	private ServerConfiguration serverConfiguration;

	public FileHandler(String file) {
		this.scannedFile = new File(file);
		this.serverConfiguration = ServerConfiguration.getInstance();
	}
	
	/**
	 * Wait the file when it's completely scanned. After that save file's information to database and send an notification email for the receiver
	 * @return result
	 */
	public Result progressingFile() {
		Command step1 = new ScanCommand(scannedFile);
		Command step2 = new MoveFileCommand(step1, scannedFile);
		Command step3 = new SaveToDatabaseCommand(step2);
		Command step4 = new SendEmailCommand(step3, serverConfiguration);
		Command finalCommand = step4.execute();
		return finalCommand.getCommandResult().getResult();
	}
}
