package com.demo.pdfGen.controller;

import com.demo.pdfGen.common.Headers;
import com.demo.pdfGen.dto.CustomerPostDTO;
import com.demo.pdfGen.logging.TrackTime;
import com.demo.pdfGen.model.Customer;
import com.demo.pdfGen.service.PdfGenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PdfGenController {

    @Autowired
    PdfGenService pdfGenService;

    @PostMapping(value = "/createCustomer")
    @TrackTime
    public Mono<Customer> createCustomer(@RequestBody CustomerPostDTO customerPostDTO){
        return pdfGenService.createCustomerForTest(customerPostDTO);
    }


    @PostMapping(value = "/pdf/{number}", produces = MediaType.APPLICATION_PDF_VALUE)
    @TrackTime
    public Mono<ResponseEntity<InputStreamResource>> createPdf(@PathVariable(value = "number") String customerNumber) throws IOException, FileNotFoundException {
        return pdfGenService.createPdf(customerNumber);
    }

}
