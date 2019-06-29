package com.stonebranch.customerinvoicesampler.service.impl;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.stonebranch.customerinvoicesampler.exception.ConfigurationNotFoundException;
import com.stonebranch.customerinvoicesampler.reader.filter.service.FileExtractionService;
import com.stonebranch.customerinvoicesampler.reader.service.SampleCustomerReaderService;
import com.stonebranch.customerinvoicesampler.service.FileProcessingService;

@Component
public class FileProcessingServiceImpl implements FileProcessingService {
	private static final int CUSTOMER_CODE_INDEX = 0;
	private static final int INVOICE_CODE_INDEX = 1;
	private static final int INVOICE_ITEM_CODE_INDEX = 0;
	public static final String CONFIG_NOT_FOUND_MSG = "Unable to process files due to incomple configuration";

	private final Charset charset = StandardCharsets.US_ASCII;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SampleCustomerReaderService sampleCustomerReaderService;

	@Autowired
	private FileExtractionService fileExtractionService;

	@Value("${incoming.customer.file}")
	private String incomingCustomerFile;

	@Value("${incoming.customer.invoice.file}")
	private String incomingCustomerInvoiceFile;

	@Value("${incoming.customer.invoice.item.file}")
	private String incomingCustomerInvoiceItemFile;

	@Value("${output.customer.file}")
	private String outputCustomerFile;

	@Value("${output.customer.invoice.file}")
	private String outputCustomerInvoiceFile;

	@Value("${output.customer.invoice.item.file}")
	private String outputCustomerInvoiceItemFile;

	@Override
	public void processIncomingFiles() {

		if (StringUtils.isEmpty(incomingCustomerFile) || StringUtils.isEmpty(incomingCustomerInvoiceFile)
				|| StringUtils.isEmpty(incomingCustomerInvoiceItemFile) || StringUtils.isEmpty(outputCustomerFile)
				|| StringUtils.isEmpty(outputCustomerInvoiceFile)
				|| StringUtils.isEmpty(outputCustomerInvoiceItemFile)) {
			throw new ConfigurationNotFoundException(CONFIG_NOT_FOUND_MSG);
		}
		logger.info("Reading sample customers");

		List<String> sampleCustomers = sampleCustomerReaderService.readSampleCustomers(charset);

		logger.info("Extracting matching customers");

		List<String> matchingCustomerCodes = fileExtractionService.extractMatchingRecords(charset,
				sampleCustomers, incomingCustomerFile, outputCustomerFile, CUSTOMER_CODE_INDEX, CUSTOMER_CODE_INDEX);

		if (matchingCustomerCodes.isEmpty()) {
			logger.info("Matching customer list is empty, nothing to process further");
			return;
		}

		logger.info("Extracting matching invoices");

		List<String> matchingInvoiceCodes = fileExtractionService.extractMatchingRecords(charset,
				matchingCustomerCodes, incomingCustomerInvoiceFile, outputCustomerInvoiceFile, CUSTOMER_CODE_INDEX, INVOICE_CODE_INDEX);

		if (matchingInvoiceCodes.isEmpty()) {
			logger.info("Matching invoices list is empty, nothing to process further");
			return;
		}

		logger.info("Extracting matching invoice items");

		fileExtractionService.extractMatchingRecords(charset, matchingInvoiceCodes, incomingCustomerInvoiceItemFile,
				outputCustomerInvoiceItemFile, INVOICE_ITEM_CODE_INDEX, INVOICE_ITEM_CODE_INDEX);

	}

}
