package com.example.pdfsigner.utils;

import org.springframework.web.multipart.MultipartFile;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;

public class CertificateUtils {

    public static boolean isValidFormat(MultipartFile certFile) {
        // Logic to validate if the certificate format is acceptable
        return true; // Placeholder
    }
    
    
    public static X509Certificate generateSelfSignedCertificate(
            KeyPair keyPair, String commonName, String organization, String country, int validityDays)
            throws Exception {

        X500Name issuerName = new X500Name("CN=" + commonName + ", O=" + organization + ", C=" + country);
        Date notBefore = new Date();
        Date notAfter = new Date(notBefore.getTime() + validityDays * 24L * 60 * 60 * 1000);
        BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                issuerName, serial, notBefore, notAfter, issuerName, keyPair.getPublic());

        ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(keyPair.getPrivate());
        return new JcaX509CertificateConverter().setProvider("BC").getCertificate(builder.build(signer));
    }

    public static byte[] createP12File(PrivateKey privateKey, X509Certificate certificate, String password) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(null, null);
        keyStore.setKeyEntry("key", privateKey, password.toCharArray(), new X509Certificate[]{certificate});

        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            keyStore.store(bos, password.toCharArray());
            return bos.toByteArray();
        }
    }

}
