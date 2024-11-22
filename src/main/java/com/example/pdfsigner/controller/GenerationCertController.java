package com.example.pdfsigner.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pdfsigner.dto.CertificateRequestDto;
import com.example.pdfsigner.service.GenerationCertService;

@RestController
@RequestMapping("/api/certificate")
public class GenerationCertController {

	private final GenerationCertService generationCertService;

	public GenerationCertController(GenerationCertService generationCertService) {
		this.generationCertService = generationCertService;
	}

	@PostMapping("/generate-p12")
	public ResponseEntity<byte[]> generateP12(@RequestBody CertificateRequestDto request) {
		try {
			// Llama al servicio para generar el archivo P12
			byte[] p12File = generationCertService.generateP12(request);

			// Configura las cabeceras para descargar el archivo
			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.p12");

			return new ResponseEntity<>(p12File, headers, HttpStatus.OK);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}
