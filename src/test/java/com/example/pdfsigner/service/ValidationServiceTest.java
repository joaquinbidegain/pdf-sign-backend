package com.example.pdfsigner.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import com.example.pdfsigner.utils.PdfSignatureUtils;

//@SpringBootTest 
class ValidationServiceTest {

	private final ValidationService validationService = new ValidationService();

	@Test
	void testIsNotPdfSigned() throws Exception {
		// Load the test PDF from resources
		byte[] pdfNotSignedContent = Files.readAllBytes(Path.of("src/test/resources/test.pdf"));

		// Verify the PDF is not signed
		boolean isSigned = PdfSignatureUtils.isPdfSigned(pdfNotSignedContent);
		System.out.println("EL PDF ESTA FIRMADO?: " + isSigned);
		assertEquals(isSigned,false);
	}

	@Test
	void testIsSignedPdf() throws Exception {
		byte[] pdfSignedContent = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));
		
		// Verify the PDF is not signed
		boolean isSigned = PdfSignatureUtils.isPdfSigned(pdfSignedContent);
		System.out.println("EL PDF ESTA FIRMADO?: " + isSigned);
		assertEquals(isSigned,true);
	}

	@Test
	void testIsSignatureValid() throws Exception {
		// Load the test PDF from resources
		byte[] pdfContent = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));

		// Verify the signature is valid
		boolean isValid = PdfSignatureUtils.isSignatureValid(pdfContent);
		assertTrue(isValid, "The PDF signature should be valid.");
	}
	
	
	@Test
	void testIsSignatureValidWithCert() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

		try {
	        byte[] pdfContent = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));
	        String p12Path = "src/test/resources/nuevo_test.p12";
	        String password = "testpass";

	        boolean isValid = PdfSignatureUtils.isSignatureValidWithCert(pdfContent, p12Path, password);
	        System.out.println("Is the PDF signature valid? " + isValid);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	@Test
	void testIsSignatureValidWithCertWrongCertificate() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

		try {
	        byte[] pdfContent = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));
	        String p12Path = "src/test/resources/trucho/certificado-incorrecto.p12";
	        String password = "testpass";

	        boolean isValid = PdfSignatureUtils.isSignatureValidWithCert(pdfContent, p12Path, password);
	        System.out.println("Is the PDF signature valid? " + isValid);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
//	@Test
//	public void testDebugSignature() throws Exception {
//		byte[] pdfContent = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));
//		byte[] signatureContent = PdfSignatureUtils.extractSignatureContent(pdfContent);
//
//		
//        byte[] pdf = Files.readAllBytes(Path.of("src/test/resources/signed-test.pdf"));
//
//	    PdfSignatureDebugger.debugSignature(signatureContent, pdf);
//	}

}
