package com.wookes.tac.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AsD {

    private static final String a = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCtn8vokxdfV9VhIeetR098SGRc3QSFxg62Kn5UuaHfgroB2IecLPRnK7Tw7w9MFc3M9bIrI1FafalehD9B7yNoEdAXbofURkDo3zt1+gGnoGDx+y3YDj30kT9+gahaGdoP/cXlTQ6qLiILMaUFxBtn3Lz18u/8521RXyAVdXyk0QIDAQAB";

    public static Map<String, String> a(String plainText) {
        Map<String, String> map = new HashMap<>();
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] raw = secretKey.getEncoded();
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(new byte[16]));
            map.put("text", Base64.encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)), Base64.NO_WRAP));
            X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(Base64.decode(a, Base64.NO_WRAP));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(publicSpec);
            Cipher cipher2 = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher2.init(Cipher.ENCRYPT_MODE, publicKey);
            map.put("key", Base64.encodeToString(cipher2.doFinal(secretKey.getEncoded()), Base64.NO_WRAP));
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidKeySpecException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return map;
    }
}
