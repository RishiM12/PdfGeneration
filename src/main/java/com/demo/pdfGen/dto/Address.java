package com.demo.pdfGen.dto;

import com.demo.pdfGen.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
    AddressType addressType;
    String addressLine1;
    String addressLine2;
    String city;
    String stateCode;
    String countryCode;
    String pinCode;
}
