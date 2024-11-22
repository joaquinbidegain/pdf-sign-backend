package com.example.pdfsigner.service;

import com.example.pdfsigner.dto.CertificateRequestDto;
import com.example.pdfsigner.utils.CertificateUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.cert.X509Certificate;

@Service
public class GenerationCertService {

    public byte[] generateP12(CertificateRequestDto request) throws Exception {
        // Agrega el proveedor de BouncyCastle
        Security.addProvider(new BouncyCastleProvider());

        // Genera el par de claves
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(request.getKeySize());
        KeyPair keyPair = keyGen.generateKeyPair();

        // Genera el certificado auto-firmado
        X509Certificate certificate = CertificateUtils.generateSelfSignedCertificate(
                keyPair,
                request.getCommonName(),
                request.getOrganization(),
                request.getCountry(),
                request.getValidityDays()
        );

        // Genera el archivo P12
        return CertificateUtils.createP12File(keyPair.getPrivate(), certificate, request.getPassword());
    }
}
