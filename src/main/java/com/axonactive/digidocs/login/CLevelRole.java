package com.axonactive.digidocs.login;
import com.axonactive.digidocs.utils.ConfigPropertiesFileUtils;


public enum CLevelRole implements Role {
	CEO(ConfigPropertiesFileUtils.getValue("CEO.role").orElse("CEO").trim()), 
	CAO(ConfigPropertiesFileUtils.getValue("CAO.role").orElse("CAO").trim()), 
	CTO(ConfigPropertiesFileUtils.getValue("CTO.role").orElse("CTO").trim()), 
	CIO(ConfigPropertiesFileUtils.getValue("CIO.role").orElse("CIO").trim()), 
	HCMCBD(ConfigPropertiesFileUtils.getValue("HCMCBD.role").orElse("HCMCBD").trim());
	
	private String position;
	
	private CLevelRole(String position) {
		this.position = position;
	}
	
	@Override
	public String getName() {
		return name();
	}

	@Override
	public String getPosition() {
		return this.position;
	}
}
