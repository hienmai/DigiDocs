package com.axonactive.digidocs.encryption;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EncryptionException extends Exception{
	
	private Throwable cause;
}
