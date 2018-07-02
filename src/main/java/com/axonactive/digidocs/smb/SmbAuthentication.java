package com.axonactive.digidocs.smb;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.configuration.ConfigurationBOM;
import com.axonactive.digidocs.configuration.ConfigurationEntity;
import com.axonactive.digidocs.configuration.ConfigurationService;
import com.axonactive.digidocs.utils.CDIUtils;

import jcifs.smb.NtlmPasswordAuthentication;

@ApplicationScoped
public class SmbAuthentication {

	private static final Logger LOGGER = Logger.getLogger(SmbAuthentication.class);
	private NtlmPasswordAuthentication smbAuth;
	
	@PostConstruct
	private void init() {
		ConfigurationService configurationService = CDIUtils.getBean(ConfigurationService.class);
		Optional<String> domainOptional = configurationService.findByKey("target.domain")
											.flatMap(configurationService::toBom)
											.map(ConfigurationBOM::getValue);
		Optional<String> usernameOptional = configurationService.findByKey("target.auth.username")
											.flatMap(configurationService::toBom)
											.map(ConfigurationBOM::getValue);
		Optional<String> passwordOptional = configurationService.findByKey("target.auth.password")
											.flatMap(configurationService::toBom)
											.map(ConfigurationBOM::getValue);
		if(!domainOptional.isPresent()) {
			LOGGER.error("target.domain not exist");
		}
		if(!usernameOptional.isPresent()) {
			LOGGER.error("target.auth.username not exist");
		}
		if(!passwordOptional.isPresent()) {
			LOGGER.error("target.auth.password not exist");
		}
		smbAuth = new NtlmPasswordAuthentication(domainOptional.orElse(""), usernameOptional.orElse(""), passwordOptional.orElse(""));
	}
	
	public NtlmPasswordAuthentication getAuth() {
		return smbAuth;	
	}
	
}
