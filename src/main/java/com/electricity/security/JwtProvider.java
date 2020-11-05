package com.electricity.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Service
public class JwtProvider {

    @Value("${keyStore.password}")
    private String keyStorePassword;

    private KeyStore keyStore;

    @PostConstruct
    public void init() throws KeyException {
        try {
            keyStore = KeyStore.getInstance("JKS");
            InputStream resourceAsStream = getClass().getResourceAsStream("/electricity.jks");
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            throw new KeyException("An error occurred while loading keystore!");
        }

    }

    public String generateToken(Authentication authentication) throws KeyException {
        User principal = (User) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws KeyException {
        try {
            return (PrivateKey) keyStore.getKey("gmistore", keyStorePassword.toCharArray());
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            throw new KeyException("An error occurred while retrieving private key from the keystore!");
        }
    }

    public boolean validateToken(String jwt) throws KeyException {
        Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
        return true;
    }

    private PublicKey getPublicKey() throws KeyException {
        try {
            return keyStore.getCertificate("gmistore").getPublicKey();
        } catch (KeyStoreException e) {
            throw new KeyException("An error occurred while retrieving public key from the keystore!");
        }
    }

    public String getUsernameFromJwt(String token) throws KeyException {
        Claims claims = Jwts.parser()
                .setSigningKey(getPublicKey())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
