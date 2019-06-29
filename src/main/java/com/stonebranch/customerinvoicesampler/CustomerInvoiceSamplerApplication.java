package com.stonebranch.customerinvoicesampler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stonebranch.customerinvoicesampler.service.FileProcessingService;

@SpringBootApplication
public class CustomerInvoiceSamplerApplication implements CommandLineRunner {
	
	@Autowired
	private FileProcessingService fileProcessingService;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CustomerInvoiceSamplerApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
	}
	
	@Override
    public void run(String... args) throws Exception {
		fileProcessingService.processIncomingFiles();
		System.exit(0);
    }

}
