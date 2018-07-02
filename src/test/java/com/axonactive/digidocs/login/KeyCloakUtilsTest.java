package com.axonactive.digidocs.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class KeyCloakUtilsTest {
	private final static String CLIENT_ID = "DigiDocs";
	
	@Mock
	HttpServletRequest httpServletRequest;
	
	@Mock
	KeycloakSecurityContext keycloakSecurityContext;
	
	@Mock
	KeycloakPrincipal keycloakPrincipal;
	
	@Mock
	AccessToken accessToken;
	
	@Mock
	Access access;

	@InjectMocks
	KeyCloakUtils keyCloakUtils;
	
	@Test
	public void testAreadyLoggedIn() {
		PowerMockito.when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(keycloakSecurityContext);
		assertTrue(KeyCloakUtils.isLoggedIn(httpServletRequest));
	}
	
	@Test
	public void testNotLoggedIn() {
		assertFalse(KeyCloakUtils.isLoggedIn(httpServletRequest));
	}
	
	@Test
	public void testGetRoles() {
		Set<String> roles = new HashSet<String>();
		roles.add("CEO");
		roles.add("CAO");
		roles.add("CTO");
		PowerMockito.when(httpServletRequest.getUserPrincipal()).thenReturn(keycloakPrincipal);
		PowerMockito.when(keycloakPrincipal.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);
		PowerMockito.when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
		PowerMockito.when(accessToken.getResourceAccess(CLIENT_ID)).thenReturn(access);
		PowerMockito.when(access.getRoles()).thenReturn(roles);
		assertEquals(3, KeyCloakUtils.getRoles(httpServletRequest).size());
	}
	
	@Test
	public void testGetIDToken() {
		PowerMockito.when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(keycloakSecurityContext);
		PowerMockito.when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
		assertNotNull(KeyCloakUtils.getIDToken(httpServletRequest));
	}
	
	@Test
	public void testGetToken() {
		PowerMockito.when(httpServletRequest.getAttribute(Mockito.anyString())).thenReturn(keycloakSecurityContext);
		PowerMockito.when(keycloakSecurityContext.getToken()).thenReturn(accessToken);
		assertNotNull(KeyCloakUtils.getToken(httpServletRequest));
	}
}
