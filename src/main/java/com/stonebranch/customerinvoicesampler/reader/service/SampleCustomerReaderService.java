package com.stonebranch.customerinvoicesampler.reader.service;

import java.nio.charset.Charset;
import java.util.List;

public interface SampleCustomerReaderService {
	List<String> readSampleCustomers(Charset charset);
}
