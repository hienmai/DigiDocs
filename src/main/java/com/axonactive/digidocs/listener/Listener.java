package com.axonactive.digidocs.listener;

import java.util.Optional;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.log4j.Logger;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

public class Listener implements ServletContextListener {
	private static final Logger LOGGER = Logger.getLogger(Listener.class);
	private static DefaultFileMonitor fileMonitor;
	private static String directory = "source.directory";
	private static ConfigurationService configurationService = CDIUtils.getBean(ConfigurationService.class);

	/**
	 * Listen to the folder where the scanner scan to
	 * @param directory
	 * @throws FileSystemException
	 */
	public static void listenToPath(String directory) throws FileSystemException {
		Optional<ConfigurationBOM> configurationBOMOptional = configurationService.findByKey(directory).flatMap(configurationService::toBom);
		if(!configurationBOMOptional.isPresent()) {
			LOGGER.error("Can't find " + directory );
			LOGGER.error("DigiDocs is not working");
			return;
		}
		FileSystemManager fsManager = VFS.getManager();
		FileObject listendir = fsManager.resolveFile(configurationBOMOptional.get().getValue());
		fileMonitor = new DefaultFileMonitor(new FileListenerIpm());
		fileMonitor.setRecursive(true);
		fileMonitor.addFile(listendir);
		fileMonitor.start();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			LOGGER.debug("Listenning!!!!");
			listenToPath(directory);
		} catch (FileSystemException e) {
			LOGGER.debug(e.getMessage());
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		LOGGER.debug("ServletContextListener destroyed");
		if (fileMonitor != null) {
			fileMonitor.stop();
		}
	}
}