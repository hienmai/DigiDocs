package com.axonactive.digidocs.utils;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FacesContext.class, Paths.class, URL.class, URI.class})
public class FaceContextServiceTest {
	@Test
	public void testGetResource() throws MalformedURLException, URISyntaxException {
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = Mockito.mock(FacesContext.class);
		ExternalContext externalContext = Mockito.mock(ExternalContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		PowerMockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		URL fileURL = PowerMockito.mock(URL.class);
		URI fileURI = PowerMockito.mock(URI.class);
		PowerMockito.when(externalContext.getResource(Mockito.anyString())).thenReturn(fileURL);
		PowerMockito.when(fileURL.toURI()).thenReturn(fileURI);
		Path filePath = Mockito.mock(Path.class);
		PowerMockito.mockStatic(Paths.class);
		PowerMockito.when(Paths.get(Mockito.any())).thenReturn(filePath);
		FacesContextService service = new FacesContextService();
		service.getResource("/resources");
	}
	
	@Test
	public void testGetParameter() {
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = Mockito.mock(FacesContext.class);
		ExternalContext externalContext = Mockito.mock(ExternalContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		PowerMockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		HashMap<String, String> params = new HashMap<>();
		params.put("key", "AA");
		PowerMockito.when(externalContext.getRequestParameterMap()).thenReturn(params);
		FacesContextService service = new FacesContextService();
		assertEquals("AA", service.getParameter("key"));
	}
}
