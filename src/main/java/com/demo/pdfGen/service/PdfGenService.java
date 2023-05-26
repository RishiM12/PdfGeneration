package com.demo.pdfGen.service;

import com.demo.pdfGen.dto.CustomerPostDTO;
import com.demo.pdfGen.model.Customer;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public interface PdfGenService {
    Mono<Customer> createCustomerForTest(CustomerPostDTO customerPostDTO);
    Mono<ResponseEntity<InputStreamResource>> createPdf(String shipmentNumber);
}
