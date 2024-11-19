package com.example.pdfsigner.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.signatures.BouncyCastleDigest;
import com.itextpdf.signatures.DigestAlgorithms;
import com.itextpdf.signatures.IExternalDigest;
import com.itextpdf.signatures.IExternalSignature;
import com.itextpdf.signatures.PdfSigner;
import com.itextpdf.signatures.PrivateKeySignature;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

@Service
public class SignatureService {

	
	
	private static final String KEYSTORE_PASSWORD = "testpass";

    // Registrar el proveedor BouncyCastle en el constructor
    public SignatureService() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }

    public byte[] signPdf(MultipartFile pdfFile, MultipartFile certFile) {
        try {
            // Cargar el certificado X.509 y la clave privada
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(certFile.getInputStream(), KEYSTORE_PASSWORD.toCharArray());
            
            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, KEYSTORE_PASSWORD.toCharArray());
            Certificate[] chain = ks.getCertificateChain(alias);

            // Verificar que obtuvimos la clave privada y la cadena de certificados
            if (pk == null || chain == null || chain.length == 0) {
                throw new RuntimeException("No se pudo obtener la clave privada o la cadena de certificados");
            }

            // Preparar el PDF para firmar
            PdfReader reader = new PdfReader(new ByteArrayInputStream(pdfFile.getBytes()));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            
            PdfSigner signer = new PdfSigner(reader, baos, new StampingProperties());

            // Configurar la firma
            signer.setFieldName("Signature1");
            
            // Crear el digest usando explícitamente BC
            IExternalDigest digest = new BouncyCastleDigest();
            
            // Crear la firma especificando explícitamente BC como proveedor
            IExternalSignature signature = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, BouncyCastleProvider.PROVIDER_NAME);

            // Firmar el documento
            signer.signDetached(
                digest,
                signature,
                chain,
                null,
                null,
                null,
                0,
                PdfSigner.CryptoStandard.CMS
            );

            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al firmar el PDF: " + e.getMessage(), e);
        }
    }
}