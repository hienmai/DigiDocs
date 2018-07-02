package com.axonactive.digidocs.exception;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import com.axonactive.digidocs.utils.MessagePropertiesFileUtils;


@ManagedBean
@RequestScoped
public class ErrorHandler {
	public String getStatusCode() {
		String val = String.valueOf((Integer) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get("javax.servlet.error.status_code"));
		return val;
	}
	
	public String showCodeMessage() {
		return MessagePropertiesFileUtils.getValue(getStatusCode() + ".message").get();
	}
}
	
