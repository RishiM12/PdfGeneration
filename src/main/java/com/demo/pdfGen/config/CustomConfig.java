package com.demo.pdfGen.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class CustomConfig {
    @Value("${carrier-name}")
    private String carrierName;

    @Value("${carrier-service}")
    private String carrierService;
}
