package com.example.pdfsigner.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import com.example.pdfsigner.service.SignatureService;

@RestController
@RequestMapping("/api/signature")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/sign")
    public ResponseEntity<byte[]> signDocument(
            @RequestParam("pdf") MultipartFile pdfFile,
            @RequestParam("certificate") MultipartFile certFile) throws IOException {
        byte[] signedPdf = signatureService.signPdf(pdfFile, certFile);
        return ResponseEntity.ok().body(signedPdf);
    }
}
