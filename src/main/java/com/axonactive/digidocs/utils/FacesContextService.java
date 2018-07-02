package com.axonactive.digidocs.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.log4j.Logger;

@ApplicationScoped
public class FacesContextService {
	private static final Logger LOGGER = Logger.getLogger(FacesContext.class);
	public byte[] getResource(String uri) {
		ExternalContext facesContext = FacesContext.getCurrentInstance().getExternalContext();
		
		try {
			URL fileURL = facesContext.getResource(uri);
			Path filePath = Paths.get(fileURL.toURI());
			return Files.readAllBytes(filePath);
		} catch (IOException | URISyntaxException e) {
			LOGGER.error(e);
			return new byte[0];
		}
	}
	
	public String getParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}
	
	public void redirectGlobalLogout(String anotherParam) throws IOException {
		redirect(getContextPath() + "/?GLO=true" + anotherParam);
		
	}
	
	public String getContextPath() {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	}
	
	public void redirect(String url) throws IOException {
		FacesContext.getCurrentInstance().getExternalContext().redirect(url);
	}
}
