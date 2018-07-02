package com.axonactive.digidocs.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

@ManagedBean
@ViewScoped
public class ConfigurationHandler {
	@Inject
	private ConfigurationService configurationService;

	private Map<String, ConfigurationBOM> configProperties;

	@PostConstruct
	public void init() {
		configProperties = new HashMap<>(configurationService.getAllProperties());
		if(configurationService.getAllProperties().isEmpty()) {
			configProperties.put("database.salt"
					, ConfigurationBOM.builder().key("database.salt").build());
			configProperties.put("source.directory"
					, ConfigurationBOM.builder().key("source.directory").build());
			configProperties.put("target.directory"
					, ConfigurationBOM.builder().key("target.directory").build());
			configProperties.put("target.domain"
					, ConfigurationBOM.builder().key("target.domain").build());
			configProperties.put("target.auth.username"
					, ConfigurationBOM.builder().key("target.auth.username").build());
			configProperties.put("target.auth.password"
					, ConfigurationBOM.builder().key("target.auth.password").build());
		}
	}

	public boolean updateConfiguration() {
		if(configurationService.updateConfigurations(configProperties)) {
			return true;
		}
		return false;
	}

	public Map<String, ConfigurationBOM> getConfigProperties() {
		return this.configProperties;
	}

	public void generateSalt() {
		String salt = ConfigurationService.generateSalt();
		configProperties.get("database.salt").setValue(salt);
	}
	
	public void buttonAction(ActionEvent actionEvent) throws IOException {
		if(!updateConfiguration()) {
			addMessage("","Save unsuccessfully!");
		}
		else {
			addMessage("Confirmation", "Save Successfully! Please restart server");
		}
    }
     
    public void addMessage(String summary, String detail) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, summary,  detail);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }
}
