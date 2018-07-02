package com.axonactive.digidocs.session;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.axonactive.digidocs.utils.FacesContextService;

@RunWith(MockitoJUnitRunner.class)
public class SessionHandlerTest {
	
	@Mock
	FacesContextService facesContextService;
	
	@InjectMocks
	SessionHandler sessionHandler;
	
	@Test
	public void testSessionTimeoutRedirect() throws IOException {
		HttpServletRequest httpRequest = Mockito.mock(HttpServletRequest.class);
		Integer idLastFile = null;
		sessionHandler.sessionTimeoutRedirect(httpRequest, idLastFile);
		Mockito.verify(facesContextService).redirectGlobalLogout("");
	}
	
	@Test
	public void testSessionTimeoutRedirectFail() throws IOException, ServletException {
		HttpServletRequest httpRequest = Mockito.mock(HttpServletRequest.class);
		Mockito.doThrow(new ServletException()).when(httpRequest).logout();
		Integer idLastFile = null;
		sessionHandler.sessionTimeoutRedirect(httpRequest, idLastFile);
		Mockito.verify(httpRequest).logout();
	}
	
}
