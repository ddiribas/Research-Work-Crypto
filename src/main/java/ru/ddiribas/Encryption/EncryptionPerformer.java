package ru.ddiribas.Encryption;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class EncryptionPerformer {
    public static String performEncryption(FileEncryptor encryptor) throws IOException {
        if (!encryptor.src.exists()) {
            throw new FileNotFoundException("Invalid source path");
        }
        encryptor.dst.mkdir();
        if (!encryptor.dst.isDirectory()) {
            throw new FileNotFoundException("Invalid destination path");
        }
        //If keyfile is not specified, create a new one
        if (!encryptor.keyFile.exists()) {
            //New file
        	encryptor.keyFile = new File(encryptor.src.getParent(), "KeyFile.dkey");
            //Generate key
        	KeyGenerator kg = null;
            try {
                kg = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            kg.init(256);
            SecretKey sk = kg.generateKey();
            byte[] key = new byte[32];
            key = sk.getEncoded();
            //Write key
            try (OutputStream os = new FileOutputStream(encryptor.keyFile)) {
                os.write(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Encrypting
        StringBuilder information = new StringBuilder();
        information.append("Encrypting..." + "\n");
        encryptor.encrypt();

        information.append(encryptor.counter).append(" files are encrypted, ").append(encryptor.ignoredCounter).append(" files ignored");
        return information.toString();
    }
    public static String performDecryption(FileDecryptor decryptor) throws IOException, IntegrityException {

        if (!decryptor.src.exists()) {
            throw new FileNotFoundException("Invalid source path");
        }
        decryptor.dst.mkdir();
        if (!decryptor.dst.isDirectory()) {
            throw new FileNotFoundException("Invalid destination path");
        }
        if (!decryptor.keyFile.exists()) {
            throw new FileNotFoundException("Invalid keyfile");
        }

        StringBuilder information = new StringBuilder();
        byte[] key = new byte[32];

        try (InputStream is = new FileInputStream(decryptor.keyFile)) {
            is.read(key);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Decrypting
        information.append("Decrypting..." + "\n");
        decryptor.decrypt();

        information.append(decryptor.counter).append(" files are decrypted, ").append(decryptor.ignoredCounter).append(" files ignored");
        return information.toString();
    }
}
