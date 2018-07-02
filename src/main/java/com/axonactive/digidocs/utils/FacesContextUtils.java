package com.axonactive.digidocs.utils;

import java.util.Optional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ComponentSystemEvent;

public class FacesContextUtils {
	public static Optional<String> getAttribute(ComponentSystemEvent event, String name) {
		return Optional.ofNullable(event)
							.map(ComponentSystemEvent::getComponent)
							.map(UIComponent::getAttributes)
							.map(atrs -> atrs.get(name))
							.map(String::valueOf);
	}
	
	public static boolean isPostback() {
		return Optional.ofNullable(FacesContext.getCurrentInstance())
				.filter(FacesContext::isPostback)
				.isPresent();
	}
}
