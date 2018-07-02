package com.axonactive.digidocs.login;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class WebAppFilterTest {

	@Mock
	HttpSession httpSession;
	
	@Mock
	FilterChain ilterChain;
	
	@Mock
	HttpServletRequest httpServletRequest;
	
	@Mock
	HttpServletResponse httpServletResponse;
	
	@Mock
	UserController userController;
	
	@InjectMocks
	WebAppFilter webAppFilter;
	
	@Test
	public void testDoFilterWithNothing() throws IOException, ServletException {
		Mockito.when(httpServletRequest.getSession(Mockito.anyBoolean())).thenReturn(httpSession);
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn("anyString");
		webAppFilter.doFilter(httpServletRequest, httpServletResponse, ilterChain);
	}
	
	@Test
	public void testDoFilterWithResourceIdentifier() throws IOException, ServletException {
		Mockito.when(httpServletRequest.getSession(Mockito.anyBoolean())).thenReturn(httpSession);
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/javax.faces.resource");
		webAppFilter.doFilter(httpServletRequest, httpServletResponse, ilterChain);
	}
	
	@PrepareForTest({ KeyCloakUtils.class, FacesContext.class })
	@Test
	public void testDoFilterWithNoKeyCloakLoginButWithAuthenticatedURLs() throws IOException, ServletException {
		Mockito.when(httpServletRequest.getSession(Mockito.anyBoolean())).thenReturn(httpSession);
		Mockito.when(httpServletRequest.getRequestURI()).thenReturn("/index.xhtml");
		PowerMockito.mockStatic(KeyCloakUtils.class);
		Mockito.when(KeyCloakUtils.isLoggedIn(httpServletRequest)).thenReturn(false);
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = PowerMockito.mock(FacesContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		webAppFilter.doFilter(httpServletRequest, httpServletResponse, ilterChain);
	}
	
	
	
	

}
