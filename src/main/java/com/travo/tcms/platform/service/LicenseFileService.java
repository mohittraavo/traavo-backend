package com.travo.tcms.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.travo.tcms.platform.dto.LicensePayload;
import org.springframework.stereotype.Service;

@Service
public class LicenseFileService {

    private static final String AES_TRANSFORMATION =
            "AES/GCM/NoPadding";

    private static final String SIGNATURE_ALGORITHM =
            "SHA256withRSA";

    private final ObjectMapper objectMapper;

    public LicenseFileService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public byte[] generateLicenseFile(
            LicensePayload payload,
            PrivateKey signingKey) {

        try {
            byte[] payloadBytes =
                    objectMapper
                            .writeValueAsString(payload)
                            .getBytes(StandardCharsets.UTF_8);

            KeyGenerator keyGenerator =
                    KeyGenerator.getInstance("AES");

            keyGenerator.init(256);

            SecretKey aesKey =
                    keyGenerator.generateKey();

            byte[] iv = new byte[12];

            SecureRandom random =
                    new SecureRandom();

            random.nextBytes(iv);

            Cipher cipher =
                    Cipher.getInstance(AES_TRANSFORMATION);

            cipher.init(
                    Cipher.ENCRYPT_MODE,
                    aesKey,
                    new GCMParameterSpec(128, iv)
            );

            byte[] encryptedPayload =
                    cipher.doFinal(payloadBytes);

            Signature signature =
                    Signature.getInstance(
                            SIGNATURE_ALGORITHM
                    );

            signature.initSign(signingKey);

            signature.update(
                    encryptedPayload
            );

            byte[] signatureBytes =
                    signature.sign();

            LicenseFileEnvelope envelope =
                    new LicenseFileEnvelope(
                            1,
                            "AES-256-GCM",
                            Base64.getEncoder()
                                    .encodeToString(iv),
                            Base64.getEncoder()
                                    .encodeToString(
                                            encryptedPayload
                                    ),
                            Base64.getEncoder()
                                    .encodeToString(
                                            aesKey.getEncoded()
                                    ),
                            Base64.getEncoder()
                                    .encodeToString(
                                            signatureBytes
                                    )
                    );

            return objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsBytes(envelope);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to generate license file",
                    e
            );
        }
    }

    private record LicenseFileEnvelope(
            int version,
            String encryption,
            String iv,
            String encryptedPayload,
            String encryptedKey,
            String signature
    ) {}
}
