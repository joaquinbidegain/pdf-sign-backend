package com.example.pdfsigner.utils;

import org.bouncycastle.cms.*;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.util.Store;

import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Collection;

public class PdfSignatureDebugger {

    public static void debugSignature(byte[] signatureContent, byte[] pdfContent) throws Exception {
        // Registrar BouncyCastle como proveedor
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        CMSSignedData signedData = new CMSSignedData(signatureContent);

        // Obtener los certificados asociados
        Store<X509CertificateHolder> certificateStore = signedData.getCertificates();
        Collection<SignerInformation> signers = signedData.getSignerInfos().getSigners();

        System.out.println("Debugging Signature:");
        System.out.println("====================");

        for (SignerInformation signer : signers) {
            System.out.println("Signer ID: " + signer.getSID());

            // Obtener el certificado del firmante
            Collection<X509CertificateHolder> matches = certificateStore.getMatches(signer.getSID());
            if (matches.isEmpty()) {
                System.out.println("No matching certificate found for signer.");
                continue;
            }

            X509CertificateHolder certHolder = matches.iterator().next();
            X509Certificate signerCert = new JcaX509CertificateConverter()
                    .setProvider("BC")
                    .getCertificate(certHolder);

            System.out.println("Signer Certificate Subject: " + signerCert.getSubjectX500Principal());
            System.out.println("Certificate Issuer: " + signerCert.getIssuerX500Principal());
            System.out.println("Certificate Serial Number: " + signerCert.getSerialNumber());
            System.out.println("Digest Algorithm: " + signer.getDigestAlgorithmID());
            System.out.println("Signature Algorithm: " + signer.getEncryptionAlgOID());

            // Depurar el contenido firmado
            byte[] signedContent = signer.getSignature();
            System.out.println("Signed Content Length: " + signedContent.length);

            // Validar la firma
            try {
                SignerInformationVerifier verifier = new JcaSimpleSignerInfoVerifierBuilder()
                        .setProvider("BC")
                        .build(certHolder);

                if (signer.verify(verifier)) {
                    System.out.println("Signature verification successful.");
                } else {
                    System.out.println("Signature verification failed!");
                }
            } catch (Exception e) {
                System.out.println("Error during signature verification: " + e.getMessage());
            }

            System.out.println("--------------------");
        }

        System.out.println("Debugging Completed.");
    }
}
