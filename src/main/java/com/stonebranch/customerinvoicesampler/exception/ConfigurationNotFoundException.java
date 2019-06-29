package com.stonebranch.customerinvoicesampler.exception;

public class ConfigurationNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 3051291695528285916L;
	
	public ConfigurationNotFoundException(String message) {
		super(message);
	}

}
