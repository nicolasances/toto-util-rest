package com.imatz.toto.util.rest;

import org.bson.Document;

/**
 * This class is a utility for calling the ToTo Registry Microservice that provides 
 * 
 * @author nick
 *
 */
public class RESTRegistryCall extends RESTCall {

	public RESTRegistryCall(String serviceName) {
		
		super("http://localhost:8080/registry/services/" + serviceName);
		
	}
	
	@Override
	public String post(String payload) {
		
		String response = super.post(payload);

		Document doc = Document.parse(response.trim());
		
		return doc.getString("endpoint");
		
	}
	
}
