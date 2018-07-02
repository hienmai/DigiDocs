package com.axonactive.digidocs.login;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.axonactive.digidocs.utils.FacesContextService;

@RunWith(PowerMockRunner.class)
public class UserControllerTest {
	@Mock
	HttpServletRequest httpServletRequest;
	
	@InjectMocks
	UserController userController;
	
	@Mock
	FacesContextService facesContextService;
	
	@PrepareForTest({ FacesContext.class })
	@Test
	public void testHandleLogout() throws ServletException, IOException {
		PowerMockito.mockStatic(FacesContext.class);
		FacesContext facesContext = PowerMockito.mock(FacesContext.class);
		PowerMockito.when(FacesContext.getCurrentInstance()).thenReturn(facesContext);
		userController.handleLogout(httpServletRequest);
	}

}
