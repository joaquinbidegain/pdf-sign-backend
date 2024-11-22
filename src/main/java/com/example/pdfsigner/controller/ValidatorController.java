package com.example.pdfsigner.controller;

import com.example.pdfsigner.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/validate")
public class ValidatorController {

    @Autowired
    private ValidationService validationService;

    @PostMapping("/pdf")
    public ResponseEntity<String> validatePdf(
            @RequestParam("pdf") MultipartFile pdfFile) {
        try {
            boolean isSigned = validationService.isPdfSigned(pdfFile);
            if (!isSigned) {
                return ResponseEntity.ok("The PDF is not signed.");
            }

            boolean isSignatureValid = validationService.isSignatureValid(pdfFile);
            if (isSignatureValid) {
                return ResponseEntity.ok("The PDF signature is valid.");
            } else {
                return ResponseEntity.ok("The PDF signature is invalid.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during validation: " + e.getMessage());
        }
    }
}
