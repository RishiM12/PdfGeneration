package com.demo.pdfGen.dto;

import com.demo.pdfGen.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneNumber {
    AddressType phoneNumberType;
    String phoneNumber;
}
