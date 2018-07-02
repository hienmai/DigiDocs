package com.axonactive.digidocs.session;

import java.io.IOException;

import javax.faces.bean.ManagedBean;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.utils.FacesContextService;
import com.axonactive.digidocs.utils.FormatUtils;

@ManagedBean
public class SessionHandler {
	private static final Logger LOGGER = Logger.getLogger(SessionHandler.class);
	
	
	@Inject
	FacesContextService facesContextService;
	
	public void sessionTimeoutRedirect(HttpServletRequest httpRequest, Integer idLastFile) {
		try {
			httpRequest.logout();
			facesContextService.redirectGlobalLogout(idLastFile != null ? "&id=" + FormatUtils.toBase64(idLastFile.intValue() + "") : "");
		} catch (ServletException | IOException e) {
			LOGGER.error("Can't log out", e);
		}
	}
}