package com.axonactive.digidocs.email;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
public class FileBOM {

	private Integer id;
	
	private String role;
	
	private String branchSender;
	
	private String path;
	
	private String name;
	
	private long fileSize;
	
	private Date date;
	
	private Integer year;
	
	private boolean read;
	
	private String keyBase64;
	
	private String ivBase64;

}
