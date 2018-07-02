package com.axonactive.digidocs.login;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private String firstName;
	private String lastName;
	private ArrayList<String> role;
	
	public String getFullName() {
		return firstName + " " + lastName;
	}
	
}
