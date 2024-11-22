package com.example.pdfsigner.service;

import com.example.pdfsigner.dto.CertificateRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class GenerationCertServiceTest {

    @InjectMocks
    private GenerationCertService generationCertService;

    private static final String OUTPUT_DIR = "src/test/resources/keygen";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        // Asegúrate de que la carpeta de salida exista
        Path outputDir = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
    }

    @Test
    void testGenerateP12AndSaveToFile() {
        // Configura el request con datos de prueba
    	CertificateRequestDto request = new CertificateRequestDto();
        request.setCommonName("Test User");
        request.setOrganization("Test Organization");
        request.setCountry("US");
        request.setValidityDays(365);
        request.setPassword("testPassword");
        request.setKeySize(2048);

        try {
            // Llama al servicio para generar el archivo P12
            byte[] p12File = generationCertService.generateP12(request);

            // Verifica que el archivo P12 no sea nulo y tenga contenido
            assertNotNull(p12File, "The generated P12 file should not be null");
            assertTrue(p12File.length > 0, "The generated P12 file should not be empty");

            // Define la ruta donde se guardará el archivo
            Path outputFilePath = Paths.get(OUTPUT_DIR, "certificate.p12");

            // Guarda el archivo P12 en la ruta especificada
            Files.write(outputFilePath, p12File);

            // Verifica que el archivo se haya creado
            assertTrue(Files.exists(outputFilePath), "The P12 file should be created in the output directory");
            System.out.println("P12 file saved to: " + outputFilePath.toAbsolutePath());

        } catch (Exception e) {
            fail("Exception thrown during P12 generation: " + e.getMessage());
        }
    }
}
