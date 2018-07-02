package com.axonactive.digidocs.smb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

@ApplicationScoped
public class SmbClient {

	private static final Logger LOGGER = Logger.getLogger(SmbClient.class);

	@Inject
	private SmbAuthentication authentication;

	static {
		jcifs.Config.setProperty("jcifs.resolveOrder", "DNS");
	}

	public boolean writeFile(String path, byte[] data) {
		if (!createNewFolder(path.substring(0, path.lastIndexOf('/')))) {
			return false;
		}
		SmbFile sFile;
		try {
			sFile = new SmbFile(path, authentication.getAuth());
		} catch (MalformedURLException e) {
			LOGGER.error("Can't login to folder", e);
			return false;
		}
		try(BufferedOutputStream sfos = new BufferedOutputStream(new SmbFileOutputStream(sFile))) {
			sfos.write(data);
		} catch (Exception e) {
			LOGGER.error("Write file error " + path, e);
			return false;
		}
		
		return true;
	}

	public byte[] readFile(String path) {

		try {
			SmbFile sFile = new SmbFile(path, authentication.getAuth());
			BufferedInputStream inStream = new BufferedInputStream(new SmbFileInputStream(sFile));
			byte[] readFile = IOUtils.toByteArray(inStream);
			inStream.close();
			return readFile;
		} catch (IOException e) {
			LOGGER.error("Read file error", e);
			return new byte[0];
		}
	}

	public boolean createNewFolder(String path) {
		try {
			SmbFile sFolder = new SmbFile(path, authentication.getAuth());
			if (sFolder.isDirectory() && sFolder.exists()) {
				return true;
			}
			sFolder.mkdirs();
		} catch (MalformedURLException | SmbException e) {
			LOGGER.error("Can't create a new folder", e);
			return false;
		}
		return true;

	}

}