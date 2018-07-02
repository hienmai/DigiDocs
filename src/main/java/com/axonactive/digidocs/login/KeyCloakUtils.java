package com.axonactive.digidocs.login;

import java.util.ArrayList;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;

public class KeyCloakUtils {
	
	private final static String CLIENT_ID = "DigiDocs"; //get this from keycloak server
	
	public static boolean isLoggedIn(HttpServletRequest req) {
        return getSession(req) != null;
    }
	
	/**
	 * Get list role of the user
	 * @param request
	 * @return roles
	 */
    public static ArrayList<String> getRoles(HttpServletRequest request) {
    	KeycloakPrincipal principal = (KeycloakPrincipal) request.getUserPrincipal();
    	Set<String> roles = principal.getKeycloakSecurityContext().getToken().getResourceAccess(CLIENT_ID).getRoles();
    	return new ArrayList<String>(roles);
    }

    public static AccessToken getIDToken(HttpServletRequest req) {
        return getSession(req).getToken();
    }

    public static AccessToken getToken(HttpServletRequest req) {
        return getSession(req).getToken();
    }

    private static KeycloakSecurityContext getSession(HttpServletRequest req) {
        return (KeycloakSecurityContext) req.getAttribute(KeycloakSecurityContext.class.getName());
    }

}
