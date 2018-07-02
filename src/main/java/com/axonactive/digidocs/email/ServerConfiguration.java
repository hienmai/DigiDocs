package com.axonactive.digidocs.email;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.axonactive.digidocs.utils.ConfigPropertiesFileUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class ServerConfiguration {
	@Getter @Setter
	private String apiSendMailURL;
	@Getter @Setter
	private String tokenKey;

	private static ServerConfiguration instance;
	private static final Logger LOGGER = Logger.getLogger(ServerConfiguration.class);

	private ServerConfiguration() {
	}

	public static ServerConfiguration getInstance() {
		if (instance == null) {
			Optional<String> apiSendMailURLOptional = ConfigPropertiesFileUtils.getValue("mail.api.url.sendMail");
			if (!apiSendMailURLOptional.isPresent()) {
				LOGGER.error("mail.api.url.sendMail not exist");
				return instance;
			}
			instance = new ServerConfiguration(apiSendMailURLOptional.get());
			Optional<String> apiGetTokenKeyURLOptional = ConfigPropertiesFileUtils.getValue("mail.api.url.getTokenKey");
			if (!apiGetTokenKeyURLOptional.isPresent()) {
				LOGGER.error("mail.api.url.getTokenKey not exist");
				return instance;
			}
			instance.refreshTokenKey();
		}
		return instance;
	}

	public ServerConfiguration(String apiSendMailURL) {
		this();
		this.apiSendMailURL = apiSendMailURL;
	}

	/**
	 * Get the new Token Key when it's expired
	 * 
	 * @return boolean
	 */
	public boolean refreshTokenKey() {
		Optional<String> apiGetTokenKeyURLOptional = ConfigPropertiesFileUtils.getValue("mail.api.url.getTokenKey");
		if (!apiGetTokenKeyURLOptional.isPresent()) {
			LOGGER.error("mail.api.url.getTokenKey not exist");
			return false;
		}
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(apiGetTokenKeyURLOptional.get().trim());
		request.addHeader("Content-Type", "application/json");
		try {
			HttpResponse httpResponse = httpClient.execute(request);
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				LOGGER.error("Fail to get Token Key: "
						+ IOUtils.toString(httpResponse.getEntity().getContent(), Charset.forName("UTF-8")));
				return false;
			}
			String contentJsonString = IOUtils.toString(httpResponse.getEntity().getContent(),
					Charset.forName("UTF-8"));
			JSONArray contentJsonArray = new JSONArray(contentJsonString);
			LOGGER.debug("TOKEN KEY : " + contentJsonArray.getString(0));
			if (contentJsonArray.getString(0).equals("null")) {
				LOGGER.error("The response is null, check the mail.api.url.getTokenKey");
				return false;
			}
			this.tokenKey = contentJsonArray.getString(0);
		} catch (IOException e) {
			LOGGER.error("Send request to ask a new token", e);
			return false;
		}
		LOGGER.debug("Get token key successfully");
		return true;
	}
}
