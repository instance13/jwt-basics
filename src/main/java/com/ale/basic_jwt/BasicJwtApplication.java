package com.ale.basic_jwt;

import javax.crypto.SecretKey;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@SpringBootApplication
public class BasicJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicJwtApplication.class, args);
		// String base64Key = Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
		// System.out.println(base64Key);
		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // returns 256-bit key
    String base64Key = Encoders.BASE64.encode(key.getEncoded());
    System.out.println("Base64 JWT key: " + base64Key);
	}
}
