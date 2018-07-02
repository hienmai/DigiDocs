package com.axonactive.digidocs.listener;

import java.io.File;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.log4j.Logger;

import com.axonactive.digidocs.common.Branch;
import com.axonactive.digidocs.email.FileHandler;
import com.axonactive.digidocs.login.RoleFactory;
import com.axonactive.digidocs.utils.FileUtils;

public class FileListenerIpm implements FileListener {
	private static final Logger LOGGER = Logger.getLogger(FileListenerIpm.class);

	@Override
	public void fileChanged(FileChangeEvent arg0) throws Exception {
		// nothing to do
	}
	
	/**
	 * Catch the create event when it's scanned
	 * @param arg0
	 */
	@Override
	public void fileCreated(FileChangeEvent arg0) throws Exception {
		FileObject fileNameObject = arg0.getFile();
		String fileName = fileNameObject.getName().toString().split("file:")[1];
		Branch branch = FileUtils.getBranch(new File(fileName));
		if (branch == Branch.NONE || !FileUtils.getRecipientRole(new File(fileName)).isPresent()) {
			LOGGER.error("Wrong format file: " + fileName);
			return;
		}
		if (!RoleFactory.checkAvailableRole(FileUtils.getRecipientRole(new File(fileName)).get())) {
			LOGGER.error("Unvalid Role: " + FileUtils.getRecipientRole(new File(fileName)).get());
			return;
		}
		CompletableFuture.supplyAsync(new FileHandler(fileName)::progressingFile).handle((result, ex) -> {
			if (result != null) {
				return result;
			}
			LOGGER.error("FAIL ", ex);
			return ex;
		});// send it to FileHandler

	}

	@Override
	public void fileDeleted(FileChangeEvent arg0) throws Exception {
		// nothing to do
	}

}
