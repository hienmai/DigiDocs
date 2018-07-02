package com.axonactive.digidocs.email;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.axonactive.digidocs.utils.FormatUtils;

public class EmailPayloadTest {

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

		EmailPayload emailPayload = new EmailPayload(ServerConfiguration.getInstance(), emailInformation);

		assertArrayEquals(
				new String[] { FormatUtils.toBase64(to), FormatUtils.toBase64(cc), FormatUtils.toBase64(bcc),
						FormatUtils.toBase64(subject), FormatUtils.toBase64(content), FormatUtils.toBase64(replyTo),
						FormatUtils.toBase64(signature) },
				new String[] { emailPayload.getTo(), emailPayload.getCc(), emailPayload.getBcc(),
						emailPayload.getSubject(), emailPayload.getContent(), emailPayload.getReplyTo(),
						emailPayload.getSignature() });

		assertArrayEquals(FormatUtils.toBase64(attachments), emailPayload.getAttachments());
	}
}
