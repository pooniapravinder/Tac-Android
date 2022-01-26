package com.wookes.tac.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class GenerateFileName {
    public static String getFileName(String name) {
        try {
            return new String(Base64.encode(MessageDigest.getInstance("SHA-256").digest(name.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP)).replaceAll("[^a-zA-Z0-9]", "");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (UUID.randomUUID()).toString().replace("-", "");
    }
}
