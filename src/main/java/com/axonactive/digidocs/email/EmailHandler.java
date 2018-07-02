package com.axonactive.digidocs.email;

import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class EmailHandler {

	private static final Logger LOGGER = Logger.getLogger(EmailHandler.class);

	private EmailHandler() {

	}

	/**
	 * Send email
	 * 
	 * @param emailInfomation
	 * @param serverConfiguration
	 * @return boolean
	 */
	public static boolean sendMail(EmailInformation emailInformation, ServerConfiguration serverConfiguration) {
		EmailPayload emailPayload = new EmailPayload(serverConfiguration, emailInformation);
		JSONObject emailPayloadJsonObject = new JSONObject(emailPayload);
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(serverConfiguration.getApiSendMailURL().trim());
		request.addHeader("Content-Type", "application/json");
		HttpEntity emailPayloadEntity;
		try {

			emailPayloadEntity = new StringEntity(emailPayloadJsonObject.toString());
			request.setEntity(emailPayloadEntity);
			HttpResponse httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				LOGGER.debug("Sent The Email Successfully");
				return true;
			}
			String resultJsonString = IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8"));
			JSONObject resultJsonObject = new JSONObject(resultJsonString);
			// Handle when token key is expired. Try again
			if (resultJsonObject.getString("responseMessage").trim().startsWith("EWS416")
					|| serverConfiguration.getTokenKey() == null) {
				LOGGER.debug("Try to refresh the token to send the email again");
				if (serverConfiguration.refreshTokenKey()) {
					emailPayload = new EmailPayload(serverConfiguration, emailInformation);
					emailPayloadJsonObject = new JSONObject(emailPayload);
					emailPayloadEntity = new StringEntity(emailPayloadJsonObject.toString());
					request.setEntity(emailPayloadEntity);
					httpResponse = httpClient.execute(request);
					if (httpResponse.getStatusLine().getStatusCode() == 200) {
						LOGGER.debug("Sent The Email Successfully after refreshing the token"); 
						return true;
					}
				}
				LOGGER.debug("Skip sending email because cant refresh the token");
			}
			LOGGER.error("Cant send the email: " + resultJsonString);
			return false;
		} catch (IOException e) {
			LOGGER.error("Send email", e);
			return false;
		}
	}

}