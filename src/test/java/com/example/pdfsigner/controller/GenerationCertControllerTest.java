package com.example.pdfsigner.controller;

import com.example.pdfsigner.dto.CertificateRequestDto;
import com.example.pdfsigner.service.GenerationCertService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(GenerationCertController.class)
class GenerationCertControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private GenerationCertService generationCertService;

    @InjectMocks
    private GenerationCertController generationCertController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGenerateP12Endpoint() throws Exception {
        // Crea un request de prueba
        CertificateRequestDto request = new CertificateRequestDto();
        request.setCommonName("Test User");
        request.setOrganization("Test Organization");
        request.setCountry("US");
        request.setValidityDays(365);
        request.setPassword("testPassword");
        request.setKeySize(2048);

        byte[] mockP12File = "mockP12FileContent".getBytes();
        Mockito.when(generationCertService.generateP12(Mockito.any(CertificateRequestDto.class)))
               .thenReturn(mockP12File);

        // Simula la solicitud POST
        mockMvc.perform(post("/api/certificate/generate-p12")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=certificate.p12"));
    }
}
