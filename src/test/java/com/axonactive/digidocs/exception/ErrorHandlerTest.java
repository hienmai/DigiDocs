package com.axonactive.digidocs.exception;

import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(FacesContext.class)
public class ErrorHandlerTest {
	
	@Mock FacesContext facesContext;
	@Mock ExternalContext externalContext;
	@Mock Map<String, Object> requestMap;
	
	@Test
	public void testGetStatusCode() {
		PowerMockito.mockStatic(FacesContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		PowerMockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		PowerMockito.when(externalContext.getRequestMap()).thenReturn(requestMap);
		PowerMockito.when(requestMap.get("javax.servlet.error.status_code")).thenReturn(404);
		
		ErrorHandler errorHandler = new ErrorHandler();
		Assert.assertEquals("404", errorHandler.getStatusCode());
	}
	
	@Test
	public void testShowCodeMessage() {
		PowerMockito.mockStatic(FacesContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		PowerMockito.when(facesContext.getExternalContext()).thenReturn(externalContext);
		PowerMockito.when(externalContext.getRequestMap()).thenReturn(requestMap);
		PowerMockito.when(requestMap.get("javax.servlet.error.status_code")).thenReturn(404);
		ErrorHandler errorHandler = new ErrorHandler();
		String notFoundMessage = "Page not found!!! Please back to previous page";
		Assert.assertEquals(notFoundMessage, errorHandler.showCodeMessage());
	}
}
