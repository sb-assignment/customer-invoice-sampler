package com.stonebranch.customerinvoicesampler.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.test.util.ReflectionTestUtils;

import com.stonebranch.customerinvoicesampler.exception.ConfigurationNotFoundException;
import com.stonebranch.customerinvoicesampler.exception.CustomerSampleListEmptyException;
import com.stonebranch.customerinvoicesampler.reader.filter.service.impl.FileExtractionServiceImpl;
import com.stonebranch.customerinvoicesampler.reader.service.impl.SampleCustomerReaderServiceImpl;
import com.stonebranch.customerinvoicesampler.service.impl.FileProcessingServiceImpl;

@RunWith(JUnit4.class)
public class FileProcessingServiceTest {

	@Rule
	public TemporaryFolder inputFolder = new TemporaryFolder();
	
	@Rule
	public TemporaryFolder outputFolder = new TemporaryFolder();
	
	private FileProcessingServiceImpl fileProcessingService = new FileProcessingServiceImpl();
	
	private File sampleCustomerFile;
	private File inputCustomerFile;
	private File inputInvoiceFile;
	private File inputInvoiceItemFile;
	private File outputCustomerFile;
	private File outputInvoiceFile;
	private File outputInvoiceItemFile;
	
	
	
	@Before
	public void init() throws IOException {
		sampleCustomerFile = inputFolder.newFile("SAMPLE_CUSTOMER.CSV");
		inputCustomerFile = inputFolder.newFile("CUSTOMER.CSV");
		inputInvoiceFile = inputFolder.newFile("INVOICE.CSV");
		inputInvoiceItemFile = inputFolder.newFile("INVOICE_ITEM.CSV");
		outputCustomerFile = inputFolder.newFile("CUSTOMER_OUTPUT.CSV");
		outputInvoiceFile = inputFolder.newFile("INVOICE_OUTPUT.CSV");
		outputInvoiceItemFile = inputFolder.newFile("INVOICE_ITEM_OUTPUT.CSV");
		
		FileExtractionServiceImpl fileExtractionService = new FileExtractionServiceImpl();
		SampleCustomerReaderServiceImpl sampleCustomerReaderService = new SampleCustomerReaderServiceImpl();
		System.out.println(sampleCustomerFile.getAbsolutePath());
		
		ReflectionTestUtils.setField(fileProcessingService, "incomingCustomerFile", inputCustomerFile.getAbsolutePath());
		ReflectionTestUtils.setField(fileProcessingService, "incomingCustomerInvoiceFile", inputInvoiceFile.getAbsolutePath());
		ReflectionTestUtils.setField(fileProcessingService, "incomingCustomerInvoiceItemFile", inputInvoiceItemFile.getAbsolutePath());
		ReflectionTestUtils.setField(fileProcessingService, "outputCustomerFile", outputCustomerFile.getAbsolutePath());
		ReflectionTestUtils.setField(fileProcessingService, "outputCustomerInvoiceFile", outputInvoiceFile.getAbsolutePath());
		ReflectionTestUtils.setField(fileProcessingService, "outputCustomerInvoiceItemFile", outputInvoiceItemFile.getAbsolutePath());
		
		ReflectionTestUtils.setField(fileProcessingService, "fileExtractionService", fileExtractionService);
		ReflectionTestUtils.setField(fileProcessingService, "sampleCustomerReaderService", sampleCustomerReaderService);
		
		ReflectionTestUtils.setField(sampleCustomerReaderService, "customerSampleFile", sampleCustomerFile.getAbsolutePath());
		
	}
	
	@Test
	public void testProcessFilesWithMatchingRecords() throws IOException {
		List<String> sampleCustomers = new ArrayList<>();
		List<String> inputCustomers = new ArrayList<>();
		List<String> inputInvoices = new ArrayList<>();
		List<String> inputInvoiceItems = new ArrayList<>();
		
		sampleCustomers.add("\"CUSTOMER_CODE\"");
		sampleCustomers.add("\"CUST0000010231\"");
		
		inputCustomers.add("\"CUSTOMER_CODE\",\"FIRSTNAME\",\"LASTNAME\"");
		inputCustomers.add("\"CUST0000010231\",\"Maria\",\"Alba\"");
		
		inputInvoices.add("\"CUSTOMER_CODE\",\"INVOICE_CODE\",\"AMOUNT\",\"DATE\"");
		inputInvoices.add("\"CUST0000010231\",\"IN0000001\",\"105.50\",\"01-Jan-2016\"");
		
		inputInvoiceItems.add("\"INVOICE_CODE\",\"ITEM_CODE\",\"AMOUNT\",\"QUANTITY\"");
		inputInvoiceItems.add("\"IN0000001\",\"MEIJI\",\"75.60\",\"100\"");
		
		Files.write(Paths.get(sampleCustomerFile.getAbsolutePath()), sampleCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputCustomerFile.getAbsolutePath()), inputCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceFile.getAbsolutePath()), inputInvoices, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceItemFile.getAbsolutePath()), inputInvoiceItems, StandardCharsets.US_ASCII);
		
		fileProcessingService.processIncomingFiles();
		
		assertTrue(Files.readAllLines(Paths.get(outputCustomerFile.getAbsolutePath())).stream().allMatch(s -> inputCustomers.contains(s)));
		assertTrue(Files.readAllLines(Paths.get(outputInvoiceFile.getAbsolutePath())).stream().allMatch(s -> inputInvoices.contains(s)));
		assertTrue(Files.readAllLines(Paths.get(outputInvoiceItemFile.getAbsolutePath())).stream().allMatch(s -> inputInvoiceItems.contains(s)));
	}
	
	@Test
	public void testProcessFilesWithNonMatchingRecords() throws IOException {
		List<String> sampleCustomers = new ArrayList<>();
		List<String> inputCustomers = new ArrayList<>();
		List<String> inputInvoices = new ArrayList<>();
		List<String> inputInvoiceItems = new ArrayList<>();
		
		sampleCustomers.add("\"CUSTOMER_CODE\"");
		sampleCustomers.add("\"CUST0000010231\"");
		
		inputCustomers.add("\"CUSTOMER_CODE\",\"FIRSTNAME\",\"LASTNAME\"");
		inputCustomers.add("\"CUST0000010231\",\"Maria\",\"Alba\"");
		inputCustomers.add("\"CUST0000010234\",\"Shannon\",\"Alba\"");
		
		inputInvoices.add("\"CUSTOMER_CODE\",\"INVOICE_CODE\",\"AMOUNT\",\"DATE\"");
		inputInvoices.add("\"CUST0000010231\",\"IN0000001\",\"105.50\",\"01-Jan-2016\"");
		inputInvoices.add("\"CUST0000010234\",\"IN0000002\",\"95.50\",\"01-Jan-2016\"");
		
		inputInvoiceItems.add("\"INVOICE_CODE\",\"ITEM_CODE\",\"AMOUNT\",\"QUANTITY\"");
		inputInvoiceItems.add("\"IN0000001\",\"MEIJI\",\"75.60\",\"100\"");
		inputInvoiceItems.add("\"IN0000002\",\"AIWAJ\",\"65.50\",\"10\"");
		
		Files.write(Paths.get(sampleCustomerFile.getAbsolutePath()), sampleCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputCustomerFile.getAbsolutePath()), inputCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceFile.getAbsolutePath()), inputInvoices, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceItemFile.getAbsolutePath()), inputInvoiceItems, StandardCharsets.US_ASCII);
		
		fileProcessingService.processIncomingFiles();
		
		List<String> outputCustomers = Files.readAllLines(Paths.get(outputCustomerFile.getAbsolutePath()));
		List<String> outputInvoices = Files.readAllLines(Paths.get(outputInvoiceFile.getAbsolutePath()));
		List<String> outputInvoiceItems = Files.readAllLines(Paths.get(outputInvoiceItemFile.getAbsolutePath()));
		
		assertFalse(inputCustomers.stream().allMatch(s -> outputCustomers.contains(s)));
		assertFalse(inputInvoices.stream().allMatch(s -> outputInvoices.contains(s)));
		assertFalse(inputInvoiceItems.stream().allMatch(s -> outputInvoiceItems.contains(s)));
	}
	
	@Test
	public void testProcessFilesCustomerNoMatch() throws IOException {
		
		List<String> sampleCustomers = new ArrayList<>();
		List<String> inputCustomers = new ArrayList<>();
		List<String> inputInvoices = new ArrayList<>();
		List<String> inputInvoiceItems = new ArrayList<>();
		
		sampleCustomers.add("\"CUSTOMER_CODE\"");
		sampleCustomers.add("\"CUST0000010231\"");
		
		inputCustomers.add("\"CUSTOMER_CODE\",\"FIRSTNAME\",\"LASTNAME\"");
		inputCustomers.add("\"CUST0000010232\",\"Maria\",\"Alba\"");
		
		inputInvoices.add("\"CUSTOMER_CODE\",\"INVOICE_CODE\",\"AMOUNT\",\"DATE\"");
		inputInvoices.add("\"CUST0000010232\",\"IN0000001\",\"105.50\",\"01-Jan-2016\"");
		
		inputInvoiceItems.add("\"INVOICE_CODE\",\"ITEM_CODE\",\"AMOUNT\",\"QUANTITY\"");
		inputInvoiceItems.add("\"IN0000002\",\"MEIJI\",\"75.60\",\"100\"");
		
		Files.write(Paths.get(sampleCustomerFile.getAbsolutePath()), sampleCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputCustomerFile.getAbsolutePath()), inputCustomers, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceFile.getAbsolutePath()), inputInvoices, StandardCharsets.US_ASCII);
		Files.write(Paths.get(inputInvoiceItemFile.getAbsolutePath()), inputInvoiceItems, StandardCharsets.US_ASCII);
		
		fileProcessingService.processIncomingFiles();
		
		assertTrue(Files.readAllLines(Paths.get(outputCustomerFile.getAbsolutePath())).get(0).equals(inputCustomers.get(0)));
		assertTrue(Files.readAllLines(Paths.get(outputInvoiceFile.getAbsolutePath())).isEmpty());
		assertTrue(Files.readAllLines(Paths.get(outputInvoiceItemFile.getAbsolutePath())).isEmpty());
	}
	
	@Test(expected=CustomerSampleListEmptyException.class)
	public void testProcessFilesSampleCustomersEmpty() throws IOException {
		
		List<String> sampleCustomers = new ArrayList<>();
		
		sampleCustomers.add("\"CUSTOMER_CODE\"");
		
		Files.write(Paths.get(sampleCustomerFile.getAbsolutePath()), sampleCustomers, StandardCharsets.US_ASCII);
		
		fileProcessingService.processIncomingFiles();
	}
	
	@Test(expected=ConfigurationNotFoundException.class)
	public void testIncompleteConfiguration() {
		ReflectionTestUtils.setField(fileProcessingService, "incomingCustomerFile", null);
		fileProcessingService.processIncomingFiles();
	}
	

}
