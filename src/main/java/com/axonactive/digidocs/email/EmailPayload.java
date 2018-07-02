package com.axonactive.digidocs.email;

import static com.axonactive.digidocs.utils.FormatUtils.toBase64;

import lombok.Getter;

@Getter
public class EmailPayload {

	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String content;
	private String replyTo;
	private String signature;
	private String[] attachments;
	private String tokenKey;
	
	private EmailPayload() {
		
	}
	public EmailPayload(ServerConfiguration serverConfiguration, EmailInformation emailInformation) {
		this.to = toBase64(emailInformation.getTo());
		this.cc = toBase64(emailInformation.getCc());
		this.bcc = toBase64(emailInformation.getBcc());
		this.subject = toBase64(emailInformation.getSubject());
		this.content = toBase64(emailInformation.getContent());
		this.replyTo = toBase64(emailInformation.getReplyTo());
		this.signature = toBase64(emailInformation.getSignature());
		this.attachments = toBase64(emailInformation.getAttachments());
		this.tokenKey = serverConfiguration.getTokenKey();
	}
}
