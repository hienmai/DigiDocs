package com.axonactive.digidocs.email;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class EmailInformationTest {

	@Test
	public void testSetSuccessfully() {

		String to = "to";
		String cc = "cc";
		String bcc = "bcc";
		String subject = "subject";
		String content = "content";
		String replyTo = "replyTo";
		String signature = "signature";
		String[] attachments = { "attachments" };

		EmailInformation emailInformation = EmailInformation.builder().attachments(attachments).bcc(bcc).cc(cc)
				.content(content).replyTo(replyTo).signature(signature).subject(subject).to(to).build();
		assertArrayEquals(new String[] { to, cc, bcc, subject, content, replyTo, signature },
				new String[] { emailInformation.getTo(), emailInformation.getCc(), emailInformation.getBcc(),
						emailInformation.getSubject(), emailInformation.getContent(), emailInformation.getReplyTo(),
						emailInformation.getSignature() });
		assertArrayEquals(attachments, emailInformation.getAttachments());
	}

}
