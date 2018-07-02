package com.axonactive.digidocs.email;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class EmailHandlerTest {
	@Test
	public void shouldSendEmailSuccessfully() {
		ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
		EmailInformation emailInformation = EmailInformation.builder().to("quang.le@axonactive.com")
				.subject("This is a testing mail").content("This is the content").build();

		boolean result = EmailHandler.sendMail(emailInformation, serverConfiguration);
		assertTrue(result);
	}

	@Test
	public void shouldSendEmailUnsuccessfully() {
		ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
		EmailInformation emailInformation = EmailInformation.builder().to("abc").subject("This is a testing mail")
				.content("This is the content").build();
		boolean result = EmailHandler.sendMail(emailInformation, serverConfiguration);
		assertFalse(result);
	}

	@Test
	public void shouldSendEmailSuccessfullyAfterRefreshToken() {
		ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
		EmailInformation emailInformation = EmailInformation.builder().to("quang.le@axonactive.com")
				.subject("This is a testing mail").content("This is the content").build();
		serverConfiguration.setTokenKey("a3dffaa05e6215f72878decdf80c26ce");
		boolean result = EmailHandler.sendMail(emailInformation, serverConfiguration);
		assertTrue(result);
	}

	@Test
	public void shouldSendEmailUnSuccessfullyAfterRefreshToken() {
		ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
		EmailInformation emailInformation = EmailInformation.builder().to("abc").subject("This is a testing mail")
				.content("This is the content").build();
		serverConfiguration.setTokenKey("a3dffaa05e6215f72878decdf80c26ce");
		boolean result = EmailHandler.sendMail(emailInformation, serverConfiguration);
		assertFalse(result);
	}

	@Test
	public void shouldSendEmailUnSuccessfullyWhenTokenIsNull() {
		ServerConfiguration serverConfiguration = ServerConfiguration.getInstance();
		EmailInformation emailInformation = EmailInformation.builder().to("abc").subject("This is a testing mail")
				.content("This is the content").build();
		serverConfiguration.setTokenKey(null);
		boolean result = EmailHandler.sendMail(emailInformation, serverConfiguration);
		assertFalse(result);
	}
}
