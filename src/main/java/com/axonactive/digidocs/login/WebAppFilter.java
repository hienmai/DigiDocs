package com.axonactive.digidocs.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.application.ResourceHandler;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter("/*")
public class WebAppFilter implements Filter {

	
	@Inject
	private UserController userController;

	@Override
	public void init(FilterConfig filterConfig) {
		// This function is used for constructing

	}

	private static final List<String> unauthenticatedURLs = new ArrayList<String>();
	private static final List<String> authenticatedURLs = new ArrayList<String>();

	
	static {
		authenticatedURLs.add("");
		authenticatedURLs.add("/");
		authenticatedURLs.add("/index");
		authenticatedURLs.add("/index.xhtml");	
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpRespone = (HttpServletResponse) response;
		HttpSession session = httpRequest.getSession(false);
		String currentURL = httpRequest.getRequestURI();
		
		httpRespone.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		httpRespone.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		httpRespone.setDateHeader("Expires", 0); // Proxies.
		
		if (currentURL.startsWith(ResourceHandler.RESOURCE_IDENTIFIER)) {
		    chain.doFilter(request, response);
		    return;
		}
		
		if(KeyCloakUtils.isLoggedIn(httpRequest)) {
			User user = new User(KeyCloakUtils.getIDToken(httpRequest).getGivenName(), KeyCloakUtils.getIDToken(httpRequest).getFamilyName(),
					KeyCloakUtils.getRoles(httpRequest));
			session.setAttribute("user", user);
			if(Roles.getRoles(user).isEmpty()) {
				httpRequest.logout();
				httpRespone.sendRedirect(httpRequest.getContextPath() +"?GLO=true");
				return;
			}
			if(unauthenticatedURLs.contains(currentURL)) {
				httpRespone.sendRedirect(httpRequest.getContextPath());
				return;
			}
		}else {
			if(authenticatedURLs.contains(currentURL)) {
				userController.handleLogout(httpRequest);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
		// This function is used for destroying

	}

}