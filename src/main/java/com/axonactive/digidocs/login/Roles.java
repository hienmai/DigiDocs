package com.axonactive.digidocs.login;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

public class Roles {
	
	private static ConfigurationService configurationService = CDIUtils.getBean(ConfigurationService.class);
	
	private Roles() {
		
	}

	public static String getRolePath(Role role) {
		Optional<String> desPath = configurationService.findByKey("target.directory")
				.flatMap(configurationService::toBom).map(ConfigurationBOM::getValue);
		return desPath.orElse("") + "/" + role.getName();
	}
	
	public static ArrayList<Role> getRoles(User user) {
		List<Role> roles = RoleFactory.getAvailableRoles();
		Predicate<Role> rolePred = r -> user.getRole().stream().anyMatch((ur -> ur.equals(r.getPosition())));
		return (ArrayList<Role>) roles.stream().filter(rolePred).collect(Collectors.toList());
	}
}
