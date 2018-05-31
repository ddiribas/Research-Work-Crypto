package ru.ddiribas.Encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class EncryptionPerformer {
    public static void performEncryption(File src, File dst, File keyFile) throws FileNotFoundException {
        byte[] key = new byte[32];

        if (!src.exists() || src == null) {
            throw new FileNotFoundException("Invalid source path");
        }
        dst.mkdir();
        if (!dst.isDirectory() || dst == null) {
            throw new FileNotFoundException("Invalid destination path");
        }

        //If keyfile is not specified, create a new one
        try (InputStream is = new FileInputStream(keyFile)) {
            is.read(key);
        } catch (IOException e) {
            keyFile = new File(src.getParent(), "KeyFile.dkey");
            KeyGenerator kg = null;
            try {
                kg = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException ee) {
                e.printStackTrace();
            }
            kg.init(256);
            SecretKey sk = kg.generateKey();
            key = sk.getEncoded();
            try (OutputStream os = new FileOutputStream(keyFile)) {
                os.write(key);
            } catch (IOException ee) {
                e.printStackTrace();
            }
        }
        //Encrypting
        System.out.println("Encrypting...");
        FileEncryptor encryptor = FileEncryptor.getEncryptor(true);
        encryptor.encrypt(src, dst, key);
        System.out.println(encryptor.counter + " files are encrypted");
    }
    public static void performDecryption(File src, File dst, File keyFile) throws FileNotFoundException {

        if (!src.exists() || src == null) {
            throw new FileNotFoundException("Invalid source path");
        }
        dst.mkdir();
        if (!dst.isDirectory() || dst == null) {
            throw new FileNotFoundException("Invalid destination path");
        }
        if (!keyFile.exists() || keyFile == null) {
            throw new FileNotFoundException("Invalid keyfile");
        }
        byte[] key = new byte[32];
        try (InputStream is = new FileInputStream(keyFile)) {
            is.read(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Decrypting...");
        FileDecryptor decryptor = FileDecryptor.getDecryptor(true);
        decryptor.decrypt(src, dst, key);
        System.out.println(decryptor.counter + " files are decrypted");
    }
}
