package com.axonactive.digidocs.login;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class RoleFactoryTest {

	 List<CLevelRole> expected =  new ArrayList<>();
	
	@Test
	public void testGetAvailableRoles() {
		expected.add(CLevelRole.CEO);
		expected.add(CLevelRole.CAO);
		expected.add(CLevelRole.CTO);
		expected.add(CLevelRole.CIO);
		expected.add(CLevelRole.HCMCBD);
		assertTrue(expected.containsAll(RoleFactory.getAvailableRoles()));
	}
	
	@Test
	public void testCheckNotAvailableRole() {
		String role = "invalidRole";
		boolean isAvailableRole = RoleFactory.checkAvailableRole(role);
		Assert.assertFalse(isAvailableRole);
	}
	
	@Test
	public void testCheckAvailableRole() {
		String role = "CEO";
		boolean isAvailableRole = RoleFactory.checkAvailableRole(role);
		Assert.assertTrue(isAvailableRole);
	}
}
