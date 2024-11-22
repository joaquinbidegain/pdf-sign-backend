package com.example.pdfsigner.utils;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationVerifier;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;

public class PdfSignatureUtils {

	public static boolean isPdfSigned(byte[] pdfContent) throws Exception {
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
			List<PDSignature> signatures = document.getSignatureDictionaries();
//            System.out.println(signatures.size());
//            System.out.println(signatures.isEmpty());
//            System.out.println(signatures.get(0).getContactInfo());
//            System.out.println(signatures.get(0).getFilter());
//            System.out.println(signatures.get(0).getName());
			return !signatures.isEmpty();
		}
	}

	public static boolean isSignatureValid(byte[] pdfContent) throws Exception {
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
			List<PDSignature> signatures = document.getSignatureDictionaries();
			if (signatures.isEmpty()) {
				return false;
			}

			for (PDSignature signature : signatures) {
				// Get the signing time
				Calendar signingTime = signature.getSignDate();

				// Validate the signature certificate
				byte[] certBytes = signature.getContents();
				CertificateFactory factory = CertificateFactory.getInstance("X.509");
				Certificate certificate = factory.generateCertificate(new ByteArrayInputStream(certBytes));

				if (certificate instanceof X509Certificate) {
					X509Certificate x509Cert = (X509Certificate) certificate;
					x509Cert.checkValidity(signingTime.getTime());
				}
			}
			return true;
		}
	}

	public static boolean isSignatureValidWithCert(byte[] pdfContent, String p12Path, String password)
			throws Exception {
		// Load the KeyStore and get the trusted certificate
		KeyStore keyStore = KeyStore.getInstance("PKCS12");
		try (FileInputStream fis = new FileInputStream(p12Path)) {
			keyStore.load(fis, password.toCharArray());
		}
		X509Certificate trustedCert = (X509Certificate) keyStore.getCertificate(keyStore.aliases().nextElement());

		// Load the PDF document
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
			List<PDSignature> signatures = document.getSignatureDictionaries();
			if (signatures.isEmpty()) {
				return false;
			}

			for (PDSignature signature : signatures) {
				// Obtener el contenido firmado original
				byte[] signedContent = signature.getSignedContent(new ByteArrayInputStream(pdfContent));

				// Obtener los bytes de la firma
				byte[] signatureBytes = signature.getContents(new ByteArrayInputStream(pdfContent));

				// Crear CMSSignedData con el contenido original
				CMSProcessableByteArray content = new CMSProcessableByteArray(signedContent);
				CMSSignedData signedData = new CMSSignedData(content, signatureBytes);

				// Debug info
				for (SignerInformation signer : signedData.getSignerInfos().getSigners()) {
					System.out.println(
							"Algoritmo de digest usado en la firma: " + signer.getDigestAlgorithmID().getAlgorithm());
					System.out.println("Algoritmo de encriptación usado: " + signer.getEncryptionAlgOID());
					System.out.println("Tipo de firma CMS: " + signedData.getSignedContentTypeOID());

					// Crear el verificador
					SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC")
							.build(new JcaX509CertificateHolder(trustedCert));

					// Verificar la firma
					if (!signer.verify(verifier)) {
						System.out.println("Verificación fallida para esta firma");
						return false;
					}
				}
			}
			return true;
		}
	}

	public static byte[] extractSignatureContent(byte[] pdfContent) throws Exception {
		try (PDDocument document = PDDocument.load(new ByteArrayInputStream(pdfContent))) {
			List<PDSignature> signatures = document.getSignatureDictionaries();
			if (signatures.isEmpty()) {
				throw new Exception("No signatures found in the PDF.");
			}

			// Extract the first signature content (assuming single signature)
			PDSignature signature = signatures.get(0);

			if (signature.getContents() != null) {
				return signature.getContents();
			} else {
				throw new Exception("No signature content available.");
			}
		}
	}

}
