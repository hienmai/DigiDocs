package com.axonactive.digidocs.email;

import java.text.MessageFormat;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.axonactive.digidocs.utils.ConfigPropertiesFileUtils;
import com.axonactive.digidocs.utils.FormatUtils;
import com.axonactive.digidocs.utils.MessagePropertiesFileUtils;

public interface EmailInformationFactory {
	
	Logger LOGGER = Logger.getLogger(EmailInformationFactory.class);

	static Optional<EmailInformation> getEmailInformation(FileBOM fileBOM) {
		Optional<String> receiverEmailOptional = ConfigPropertiesFileUtils.getValue(fileBOM.getRole() + ".mail");
		if (!receiverEmailOptional.isPresent()) {
			LOGGER.error("Cant find " + fileBOM.getRole() + ".mail");
			return Optional.empty();
		}
		String receiverEmail = receiverEmailOptional.get().trim();
		String urlToFile = ConfigPropertiesFileUtils.getValue("host.url").orElse("NOT_FOUND_DOMAIN") + "/index?id="
				+ FormatUtils.toBase64(String.valueOf(fileBOM.getId()));
		String emailSubject = MessageFormat.format(MessagePropertiesFileUtils.getValue("emailSubject").orElse(""),
				fileBOM.getBranchSender());
		String receiverName = ConfigPropertiesFileUtils.getValue(fileBOM.getRole() + ".name").get();
		String emailMessage = MessageFormat.format(MessagePropertiesFileUtils.getValue("emailMessage").orElse(""),
				receiverName, fileBOM.getBranchSender(), urlToFile);
		EmailInformation emailInformation = EmailInformation.builder().to(receiverEmail).subject(emailSubject)
				.content(emailMessage).build();
		return Optional.of(emailInformation);
	}
}
