package ru.ddiribas.Encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class EncryptionPerformer {
    public static String performEncryption(File src, File dst, File keyFile, FileEncryptor encryptor) throws FileNotFoundException {

        if (!src.exists() || src == null) {
            throw new FileNotFoundException("Invalid source path");
        }
        dst.mkdir();
        if (!dst.isDirectory() || dst == null) {
            throw new FileNotFoundException("Invalid destination path");
        }

        StringBuilder information = new StringBuilder();
        byte[] key = new byte[32];

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
        information.append("Encrypting..." + "\n");
        encryptor.encrypt(src, dst, key);

        information.append(encryptor.counter + " files are encrypted");
        return information.toString();
    }
    public static String performDecryption(File src, File dst, File keyFile, FileDecryptor decryptor) throws FileNotFoundException {

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

        StringBuilder information = new StringBuilder();
        byte[] key = new byte[32];

        try (InputStream is = new FileInputStream(keyFile)) {
            is.read(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Decrypting
        information.append("Decrypting..." + "\n");
        decryptor.decrypt(src, dst, key);

        information.append(decryptor.counter + " files are decrypted");
        return information.toString();
    }
}
