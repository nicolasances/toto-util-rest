package com.imatz.toto.util.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class provides utility methods for performing REST HTTP Calls
 * 
 * @author C308961
 *
 */
public class RESTCall {

	private static final Logger logger_ = LogManager.getLogger();

	private String endpoint_;
	private Credentials credentials_;

	public RESTCall(String endpoint) {
		endpoint_ = endpoint;
	}

	/**
	 * Executes a POST HTTP call to the specified endpoint passing the provided
	 * payload in the request's body.
	 * 
	 * @param payload
	 *            the body of the HTTP request
	 * @return a stringed version of the HTTP response
	 */
	public String post(String payload) {
		return call(payload, "POST");
	}

	/**
	 * Executes a DELETE HTTP call to the specified endpoint passing the
	 * provided payload in the request's body.
	 * 
	 * @param payload
	 *            the body of the HTTP request
	 * @return a stringed version of the HTTP response
	 */
	public String delete() {
		return call(null, "DELETE");
	}

	public String call(String payload, String method) {

		HttpURLConnection connection = null;
		try {

			logger_.info("Sending HTTP " + method + " request to URL " + endpoint_ + " with the following payload: " + payload);

			URL url = new URL(endpoint_);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod(method);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");

			if (credentials_ != null) setCredentials(connection);

			if (payload != null) {
				OutputStream os = connection.getOutputStream();
				os.write(payload.getBytes());
				os.flush();
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((connection.getInputStream())));

			String response = buildResponse(br);

			logger_.info("Sent HTTP " + method + " request to URL " + endpoint_ + " and received the following response: " + response);

			return response;

		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (connection != null) connection.disconnect();
		}

	}

	/**
	 * Sets the credentials by setting the HTTP Basic Auth header
	 * 
	 * @param connection
	 * @throws UnsupportedEncodingException 
	 */
	private void setCredentials(HttpURLConnection connection) throws UnsupportedEncodingException {

		String encoding = Base64.getEncoder().encodeToString((credentials_.getUsername() + ":" + credentials_.getPassword()).getBytes("UTF-8"));
		
		connection.setRequestProperty("Authorization", "Basic " + encoding);
	}

	/**
	 * Serializes the information provided in the reader and returns it as a
	 * string
	 * 
	 * @param the
	 *            reader
	 * @return the serialized information
	 * @throws IOException
	 */
	private String buildResponse(BufferedReader reader) throws IOException {

		StringBuffer buffer = new StringBuffer();

		String line = "";
		while ((line = reader.readLine()) != null) {
			buffer.append(" ");
			buffer.append(line);
		}

		return buffer.toString();
	}

	public void setCredentials(Credentials credentials) {
		credentials_ = credentials;
	}

}
