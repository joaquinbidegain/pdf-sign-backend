package com.example.pdfsigner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SignatureServiceTest {

    @InjectMocks
    private SignatureService signatureService;

    private MultipartFile pdfFile;
    private MultipartFile certFile;

    @BeforeEach
    void setUp() throws IOException {
        // Cargar un PDF de prueba desde resources
        String pdfPath = "src/test/resources/test.pdf";
        File pdfTestFile = new File(pdfPath);
        FileInputStream pdfInput = new FileInputStream(pdfTestFile);
        pdfFile = new MockMultipartFile(
            "test.pdf",
            "test.pdf",
            "application/pdf",
            pdfInput
        );

        // Cargar un certificado de prueba desde resources
        String certPath = "src/test/resources/nuevo_test.p12";
        File certTestFile = new File(certPath);
        FileInputStream certInput = new FileInputStream(certTestFile);
        certFile = new MockMultipartFile(
            "test-cert.p12",
            "test-cert.p12",
            "application/x-pkcs12",
            certInput
        );
    }

    @Test
    void testSignPdf_Success() throws IOException {
        // Verificar que los archivos se cargaron correctamente
        assertNotNull(pdfFile.getBytes(), "El archivo PDF no puede ser nulo");
        assertNotNull(certFile.getBytes(), "El archivo de certificado no puede ser nulo");
        assertTrue(pdfFile.getBytes().length > 0, "El archivo PDF no puede estar vacío");
        assertTrue(certFile.getBytes().length > 0, "El archivo de certificado no puede estar vacío");

        // Ejecutar
        byte[] signedPdf = signatureService.signPdf(pdfFile, certFile);

        // Verificar
        assertNotNull(signedPdf, "El PDF firmado no puede ser nulo");
        assertTrue(signedPdf.length > 0, "El PDF firmado no puede estar vacío");
        assertNotEquals(pdfFile.getBytes().length, signedPdf.length, "El PDF firmado debe ser diferente al original");
        
        // Guardar el PDF firmado para inspección visual
        Files.write(Paths.get("src/test/resources/signed-test.pdf"), signedPdf);
    }


    @Test
    void testSignPdf_InvalidPdf() {
        // Preparar un archivo PDF inválido
        MultipartFile invalidPdf = new MockMultipartFile(
            "invalid.pdf",
            "invalid.pdf",
            "application/pdf",
            "not a pdf".getBytes()
        );

        // Verificar que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            signatureService.signPdf(invalidPdf, certFile);
        });
    }

    @Test
    void testSignPdf_InvalidCertificate() {
        // Preparar un certificado inválido
        MultipartFile invalidCert = new MockMultipartFile(
            "invalid.p12",
            "invalid.p12",
            "application/x-pkcs12",
            "not a certificate".getBytes()
        );

        // Verificar que se lanza la excepción esperada
        assertThrows(RuntimeException.class, () -> {
            signatureService.signPdf(pdfFile, invalidCert);
        });
    }

    @Test
    void testSignPdf_NullInputs() {
        // Verificar que se manejan correctamente los inputs nulos
        assertThrows(NullPointerException.class, () -> {
            signatureService.signPdf(null, certFile);
        });

        assertThrows(NullPointerException.class, () -> {
            signatureService.signPdf(pdfFile, null);
        });
    }
}