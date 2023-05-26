package com.demo.pdfGen.model;

import com.demo.pdfGen.dto.Address;
import com.demo.pdfGen.dto.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationStrategy.UNIQUE)
    String id;
    String customerNumber;
    String customerName;
    String shipmentNumber;
    Address address;
    PhoneNumber phoneNumber;
}
