package com.rocket.commons.security.auth;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetTokenUtil {

  @Value("${security.reset-token.secret-key}")
  private String secretKey;

  @Value("${security.reset-token.algorithm}")
  private String algorithm;

  private Key key;
  private Cipher cipher;

  @PostConstruct
  public void init() throws Exception {
    key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
    cipher = Cipher.getInstance(algorithm);
  }

  /**
   * Reset Token 생성
   */
  public String generateResetToken() {
    return UUID.randomUUID().toString();
  }

  /**
   * 암호화
   */
  public String encrypt(String plainText) throws Exception {
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
    return Base64.getEncoder().encodeToString(encrypted);
  }

  /**
   * 복호화
   */
  public String decrypt(String encryptedText) throws Exception {
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] decoded = Base64.getDecoder().decode(encryptedText);
    byte[] decrypted = cipher.doFinal(decoded);
    return new String(decrypted, StandardCharsets.UTF_8);
  }
}