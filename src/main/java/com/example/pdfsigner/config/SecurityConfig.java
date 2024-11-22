package com.example.pdfsigner.config;

import java.security.Security;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class SecurityConfig {
    @PostConstruct
    public void init() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
