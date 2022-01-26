package com.wookes.tac.util;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    private static byte[] checksum(File filePath) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
        try (InputStream is = new FileInputStream(filePath);
             DigestInputStream dis = new DigestInputStream(is, md)) {
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
        return md.digest();
    }

    public static String getMD5(File file) {
        return new String(Base64.encode(checksum(file), Base64.NO_WRAP));
    }
}
