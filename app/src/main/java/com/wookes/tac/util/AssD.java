package com.wookes.tac.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AssD {

    private static final String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAKUMcxuBvArkODeYbDexo4PHgpEG2LrmD+teFCwGbUBwf2LQZflmJle024/iAGzF6sfl7nu2XvUkwzeIjh+0GJ9j6hwksLk3L2vXemJBnzTHxbhUnhSSfonjn3QcXAKeWOORUAcSqaCqVsUI8LW5w4dPuqJHoQEs/L/w1RnbS+oLAgMBAAECgYAmL8I2HAcg0fq7yjVHkX8Oj0uDOH1OiMooH8U1BmuinCSs/SCK2nqGkl62ZIjKoeQXSfiqgOBpgY71O0/+v9FuN/0vz7ZUwm/Gd/rlzNxlCCV42Y2AHrfv4DSmBp03YUKxpv4vifJiSFojswNv0iLO+vS4s3u2vndLcAbBdsbI4QJBANwrDPHwG6gu65GK4kHpKiqnEeXcIzirf69Q+m2Nz/zyBqaJ64/Fz5tvROmlX+aj0fmxKMURcL66TJuvciOL59sCQQC/6O7eXiUIiPyJpdvy6QwkF6kZisp3FP4UbBWlOoU/so1Edc1OWcaUBW77s8K6g7BPEbx/xJ/O6k4Q4pHlS/WRAkEAhMWpftWPuDLjeNfKbnkQJryt/HJvAyPZUn6BJ0QYI7BI9nHCDuf5tQC7DDPgy5QLcoq1zXIhDcQHovOA35LldQJAXw9z2yRJNdwdM9W7iKyVvf8WXTeZqLj2Tolng2vkezPntASiSPBBZCr42acmixsWJ2SCuctc2bLILqb8W9dvcQJBALg6s9xzFGPajphV8Z94ab+bKK+EJBoXSpePqRhh01qx9TXxm5KiOb4KOLRwh10u3Gz8hpBZVvOK2b5bPkr74f0=";

    public static String a(String secretKeyEncryption, String cipherText) {
        String plainText = "";
        try {
            PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(Base64.decode(PRIVATE_KEY, Base64.NO_WRAP));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(privateSpec);
            Cipher cipher1 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher1.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] secretKeyBytes = cipher1.doFinal(Base64.decode(secretKeyEncryption, Base64.NO_WRAP));
            SecretKey secretKey = new SecretKeySpec(secretKeyBytes, 0, secretKeyBytes.length, "AES");
            byte[] raw = secretKey.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            byte[] original = cipher.doFinal(Base64.decode(cipherText, Base64.NO_WRAP));
            plainText = new String(original, StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return plainText;
    }
}
