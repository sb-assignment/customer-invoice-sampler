package com.stonebranch.customerinvoicesampler.exception;

public class CustomerSampleListEmptyException extends RuntimeException {
	private static final long serialVersionUID = 7075444606091540062L;
	public static final String CUSTOMER_SAMPLE_FILE_EMPTY_MSG = "Customer sample file is empty";
	
	public CustomerSampleListEmptyException() {
		super(CUSTOMER_SAMPLE_FILE_EMPTY_MSG);
	}

}
