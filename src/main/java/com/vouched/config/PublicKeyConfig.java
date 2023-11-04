package com.vouched.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class PublicKeyConfig {

    @Inject
    private ResourceLoader resourceLoader;

    @Bean
    public PublicKey publicKey(
            @Value("${spring.security.oauth2.resourceserver.jwt.public-key-location}") String publicKeyFile
    ) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        try (InputStream in = resourceLoader.getResource(publicKeyFile).getInputStream()) {

            byte[] keyBytes = in.readAllBytes();

            // Remove the "-----BEGIN PUBLIC KEY-----" and "-----END PUBLIC KEY-----" lines if present
            String publicKeyPEM = new String(keyBytes)
                    .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                    .replaceAll("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
            // Use an appropriate algorithm for your public key, such as "RSA"
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }

    }
}