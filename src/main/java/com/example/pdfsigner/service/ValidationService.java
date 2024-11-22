package com.example.pdfsigner.service;

import com.example.pdfsigner.utils.PdfSignatureUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ValidationService {

    public boolean isPdfSigned(MultipartFile pdfFile) throws Exception {
        return PdfSignatureUtils.isPdfSigned(pdfFile.getBytes());
    }

    public boolean isSignatureValid(MultipartFile pdfFile) throws Exception {
        return PdfSignatureUtils.isSignatureValid(pdfFile.getBytes());
    }
}
