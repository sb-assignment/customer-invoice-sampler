package com.stonebranch.customerinvoicesampler.reader.filter.service.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.stonebranch.customerinvoicesampler.exception.CustomerInvoiceSamplerApplicationException;
import com.stonebranch.customerinvoicesampler.exception.FileNotReadableException;
import com.stonebranch.customerinvoicesampler.reader.filter.service.FileExtractionService;

@Component
public class FileExtractionServiceImpl implements FileExtractionService {

	private final int CHUNK_SIZE = 100;

	public static final String INCOMING_CUSTOMER_FILE_CONFIG_NOT_FOUND_MSG = "Incoming customer file configuration not found";
	public static final String INCOMING_CUSTOMER_FILE_NOT_READABLE_MSG = "Error while reading incoming customer file";

	@Override
	public List<String> extractMatchingRecords(Charset charset, List<String> inputElements,
			String incomingFile, String outputFile, int matchColumnIndex, int extractColumnIndex) {
		List<String> filteredElements = new ArrayList<>();
		List<String> matchedRecordsChunk = new ArrayList<>();
		String recordInfo = null;
		String[] recordElements = null;
		Path absoluteReadFilePath = Paths.get(incomingFile);
		Path absoluteWriteFilePath = Paths.get(outputFile);

		if (!Files.isReadable(absoluteReadFilePath)) {
			throw new FileNotReadableException(INCOMING_CUSTOMER_FILE_NOT_READABLE_MSG);
		}

		try (BufferedReader reader = Files.newBufferedReader(absoluteReadFilePath, charset);
				BufferedWriter writer = Files.newBufferedWriter(absoluteWriteFilePath, charset)) {
			recordInfo = reader.readLine();
			if (recordInfo == null) {
				return filteredElements;
			}
			matchedRecordsChunk.add(recordInfo);
			while (true) {
				recordInfo = reader.readLine();

				//End of file
				if (recordInfo == null) {
					break;
				}
				recordElements = recordInfo.split(",");
				if (inputElements.contains(recordElements[matchColumnIndex])) {
					if(recordElements.length > extractColumnIndex) {
						filteredElements.add(recordElements[extractColumnIndex]);
					}
					matchedRecordsChunk.add(recordInfo);
				}

				//Write chunk by chunk
				if (matchedRecordsChunk.size() == (CHUNK_SIZE + 1)) {
					writer.write(
							String.join(System.lineSeparator(), matchedRecordsChunk).concat(System.lineSeparator()));
					matchedRecordsChunk.clear();
				}
			}
			
			//Write last chunk
			if (!matchedRecordsChunk.isEmpty()) {
				writer.write(String.join(System.lineSeparator(), matchedRecordsChunk));
			}
		} catch (IOException e) {
			throw new CustomerInvoiceSamplerApplicationException(e);
		}

		return filteredElements;
	}

}
