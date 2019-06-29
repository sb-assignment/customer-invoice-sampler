package com.stonebranch.customerinvoicesampler.reader.filter.service;

import java.nio.charset.Charset;
import java.util.List;

public interface FileExtractionService {
	List<String> extractMatchingRecords(Charset charset, List<String> customerCodes,
			String incomingFileName, String outputFileName, int matchColumnIndex, int extractColumnIndex);
}
