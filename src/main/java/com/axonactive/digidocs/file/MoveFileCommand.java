package com.axonactive.digidocs.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.common.Tuple;
import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.encryption.AES256Encryption;
import com.axonactive.digidocs.encryption.SymmetricEncryption;
import com.axonactive.digidocs.encryption.SymmetricEncryptionUtils;
import com.axonactive.digidocs.smb.SmbClient;
import com.axonactive.digidocs.utils.CDIUtils;
import com.axonactive.digidocs.utils.ConfigPropertiesFileUtils;
import com.axonactive.digidocs.utils.DateUtils;
import com.axonactive.digidocs.utils.FileUtils;
import com.axonactive.digidocs.utils.FolderUtils;
import com.axonactive.digidocs.utils.FormatUtils;

import lombok.Getter;

public class MoveFileCommand implements Command {
	private static final Logger LOGGER = Logger.getLogger(MoveFileCommand.class);

	private Command previousCommand;
	@Getter
	private CommandResult commandResult;
	private File scannedFile;
	private SymmetricEncryption symmetricEncryption = new AES256Encryption();
	private String receiverRole;
	private Date processingDate = new Date();
	private SmbClient smbClient;
	private ConfigurationService configurationService;

	public MoveFileCommand(Command previousCommand, File scannedFile) {
		this.previousCommand = previousCommand;
		this.scannedFile = scannedFile;
		this.configurationService = CDIUtils.getBean(ConfigurationService.class);
		this.smbClient = CDIUtils.getBean(SmbClient.class);
	}

	private Tuple<String, String> getFileStoragePath() {
		Optional<ConfigurationBOM> targetDirectoryOptional = configurationService.findByKey("target.directory")
				.flatMap(configurationService::toBom);
		if (!targetDirectoryOptional.isPresent()) {
			LOGGER.error("target.directory not exist");
			return Tuple.empty();
		}

		String desFolderPath = targetDirectoryOptional.get().getValue().trim() + FolderUtils.getSlash()
				+ this.receiverRole + FolderUtils.getSlash() + Calendar.getInstance().get(Calendar.YEAR);
		String fileName = "#" + FileUtils.getBranch(scannedFile).name().toString() + "-"
				+ DateUtils.getDateFormat(this.processingDate)
				+ ConfigPropertiesFileUtils.getValue("file.extension").get();
		String desFilePath = desFolderPath + FolderUtils.getSlash() + fileName;

		return new Tuple<>(desFilePath, fileName);
	}

	private Tuple<String, String> moveFile() {
		byte[] data = FileUtils.getAllData(scannedFile);
		if (data.length == 0) {
			return Tuple.empty();
		}

		Tuple<String, String> fileTuple = getFileStoragePath();
		if (fileTuple == Tuple.<String, String>empty()) {
			return Tuple.empty();
		}
		data = SymmetricEncryptionUtils.encrypt(data, symmetricEncryption);
		if (data.length == 0) {
			return Tuple.empty();
		}

		if (smbClient.writeFile(fileTuple.getX(), data)) {
			try {
				Files.delete(Paths.get(scannedFile.getPath()));
			} catch (IOException e) {
				LOGGER.debug("Delete file " + scannedFile.getPath() + " unsuccessfully", e);
			}
			return fileTuple;
		}
		return Tuple.empty();
	}

	@Override
	public Command execute() {
		Command commandResult = previousCommand.execute();

		if (commandResult.getCommandResult().getResult() != Result.STEP_SUCESS) {
			return commandResult;
		}

		String receiverRole = FileUtils.getRecipientRole(scannedFile).get(); // already check in FileListenerIpm
		commandResult.getCommandResult().getFileBOMBuilder().date(new Date()).fileSize(scannedFile.length())
				.branchSender(FileUtils.getBranch(scannedFile).toString()).read(false).role(receiverRole);

		commandResult.getCommandResult().getFileBOMBuilder()
				.keyBase64(FormatUtils.toBase64(symmetricEncryption.getSecretKeyBytes()))
				.ivBase64(FormatUtils.toBase64(symmetricEncryption.getIV()));

		Tuple<String, String> fileTuple = moveFile();
		if (fileTuple == Tuple.<String, String>empty()) {
			LOGGER.error("Can't move the file " + scannedFile.getName());
			commandResult.getCommandResult().setResult(Result.FAIL_MOVING);
			return commandResult;
		}

		commandResult.getCommandResult().getFileBOMBuilder().year(Calendar.getInstance().get(Calendar.YEAR))
				.path(fileTuple.getX()).name(fileTuple.getY()).build();

		commandResult.getCommandResult().setResult(Result.STEP_SUCESS);
		return commandResult;
	}

}
