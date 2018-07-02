package com.axonactive.digidocs.email;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder (toBuilder = true)
@AllArgsConstructor (access = AccessLevel.PRIVATE)
@NoArgsConstructor (access = AccessLevel.PRIVATE)
public class EmailInformation {
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String content;
	private String replyTo;
	private String signature;
	private String[] attachments;
}
