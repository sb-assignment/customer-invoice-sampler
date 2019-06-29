package com.stonebranch.customerinvoicesampler.reader.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stonebranch.customerinvoicesampler.exception.ConfigurationNotFoundException;
import com.stonebranch.customerinvoicesampler.exception.CustomerInvoiceSamplerApplicationException;
import com.stonebranch.customerinvoicesampler.exception.CustomerSampleListEmptyException;
import com.stonebranch.customerinvoicesampler.exception.FileNotReadableException;
import com.stonebranch.customerinvoicesampler.reader.service.SampleCustomerReaderService;

@Component
public class SampleCustomerReaderServiceImpl implements SampleCustomerReaderService {
	public static final String CUSTOMER_SAMPLE_CONFIG_NOT_FOUND_MSG = "Customer sample file configuration not found";
	public static final String CUSTOMER_SAMPLE_FILE_NOT_READABLE_MSG = "Error while reading customer sample file";
	
	@Value("${customer.sample.file}")
	private String customerSampleFile;

	@Override
	public List<String> readSampleCustomers(Charset charset) {
		if(StringUtils.isEmpty(customerSampleFile)) {
			throw new ConfigurationNotFoundException(CUSTOMER_SAMPLE_CONFIG_NOT_FOUND_MSG);
		}
		
		Path absoluteFilePath = Paths.get(customerSampleFile);
		
		if(!Files.isReadable(absoluteFilePath)) {
			throw new FileNotReadableException(CUSTOMER_SAMPLE_FILE_NOT_READABLE_MSG);
		}
		List<String> sampleCustomers = new ArrayList<>();
		try(BufferedReader customerSampleReader =  Files.newBufferedReader(absoluteFilePath, charset)) {
			sampleCustomers = customerSampleReader.lines().skip(1).collect(Collectors.toList());
		} catch (IOException e) {
			throw new CustomerInvoiceSamplerApplicationException(e);
		}
		
		if(sampleCustomers.isEmpty()) {
			throw new CustomerSampleListEmptyException();
		}
		
		return sampleCustomers;
	}

}
