package com.axonactive.digidocs.utils;

import static org.junit.Assert.assertNotNull;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class SessionUtilsTest {
	
	@Before
	public void setup() {
		FacesContextMocker.mockFacesContext();

	    ExternalContext externalContext = Mockito.mock(ExternalContext.class);
	    Mockito.when(FacesContext.getCurrentInstance().getExternalContext())
	            .thenReturn(externalContext);

	    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
	    Mockito.when(
	            FacesContext.getCurrentInstance().getExternalContext()
	                    .getRequest()).thenReturn(request);

	    HttpSession httpSession = Mockito.mock(HttpSession.class);
	    Mockito.when(SessionUtils.getExistSession()).thenReturn(httpSession);
	}
	
	@Test
	public void testGetSession() {
		HttpSession session = SessionUtils.getExistSession();
		assertNotNull(session);
	}
	
	@Test
	public void testGetRequest() {
		HttpServletRequest request = SessionUtils.getRequest();
		assertNotNull(request);
	}
}
