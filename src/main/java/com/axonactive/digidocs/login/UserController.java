package com.axonactive.digidocs.login;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.axonactive.digidocs.utils.FacesContextService;

@ManagedBean
public class UserController {
	
	@Inject
	FacesContextService facesContextService;

	public void handleLogout(HttpServletRequest request) throws ServletException, IOException {
		request.logout();
		facesContextService.redirectGlobalLogout("");
	}
}
