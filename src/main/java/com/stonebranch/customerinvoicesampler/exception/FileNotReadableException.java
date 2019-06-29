package com.stonebranch.customerinvoicesampler.exception;

public class FileNotReadableException extends RuntimeException {
	private static final long serialVersionUID = 4796272229174814682L;
	
	public FileNotReadableException(String message) {
		super(message);
	}

}
