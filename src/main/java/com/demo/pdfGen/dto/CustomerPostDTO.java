package com.demo.pdfGen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPostDTO {
    String customerNumber;
    String customerName;
    String shipmentNumber;
    Address address;
    PhoneNumber phoneNumber;
}
