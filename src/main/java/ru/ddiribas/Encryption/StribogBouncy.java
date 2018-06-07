package ru.ddiribas.Encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class StribogBouncy {
    public static byte[] getByteHash512(byte[] input) {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("GOST3411-2012-512");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] output = messageDigest.digest(input);
        return output;
    }

    public static byte[] getByteHash256(byte[] input) {
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("GOST3411-2012-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        byte[] output = messageDigest.digest(input);
        return output;
    }
}
