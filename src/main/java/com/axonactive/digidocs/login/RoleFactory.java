package com.axonactive.digidocs.login;

import java.util.Arrays;
import java.util.List;

public class RoleFactory {
	private RoleFactory() {
		
	}

	public static List<Role> getAvailableRoles() {
		return Arrays.asList(CLevelRole.values());
	}
	
	public static boolean checkAvailableRole(String roleString) {
		for(Role role : getAvailableRoles()) {
			if(role.getName().equals(roleString)) {
				return true;
			}
		}
		return false;
	}
}
