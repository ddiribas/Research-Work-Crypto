package ru.ddiribas.Encryption;

import ru.ddiribas.MainApp;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.security.NoSuchAlgorithmException;

public class EncryptionPerformer {
    public static void prepareForEncryption (FileEncryptor encryptor) throws FileNotFoundException {
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
            //TODO: генерировать ключи для кузнечика
            try {
                kg = KeyGenerator.getInstance("AES");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            kg.init(256);
            SecretKey sk = kg.generateKey();
            byte[] key;
            key = sk.getEncoded();
            //Write key
            try (OutputStream os = new FileOutputStream(encryptor.keyFile)) {
                os.write(key);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (encryptor.keyFile.length() < 32) { //Проверка корректности длины файла-ключа
            throw new FileNotFoundException("Invalid key file");
        } else if (encryptor.keyFile.length() % 32 != 0 || !encryptor.keyFile.getName().substring(encryptor.keyFile.getName().lastIndexOf(".")+1).equals("dkey")) {
            MainApp.getMainController().showWarningWindow("The keyfile may not be valid for the encryption.");
            if (!MainApp.getMainController().getWarningController().continueExecution) {
                throw new FileNotFoundException("The operation was interrupted.");
            }
        }

    }
    public static void prepareForDecryption(FileDecryptor decryptor) throws FileNotFoundException {
        if (!decryptor.src.exists()) {
            throw new FileNotFoundException("Invalid source path");
        }
        decryptor.dst.mkdir();
        if (!decryptor.dst.isDirectory()) {
            throw new FileNotFoundException("Invalid destination path");
        }
        if (!decryptor.keyFile.exists() || decryptor.keyFile.length() < 32) {
            throw new FileNotFoundException("Invalid key file");
        } else if (decryptor.keyFile.length() % 32 != 0 || !decryptor.keyFile.getName().substring(decryptor.keyFile.getName().lastIndexOf(".")+1).equals("dkey")) {
            MainApp.getMainController().showWarningWindow("The keyfile may not be valid for the encryption.");
            if (!MainApp.getMainController().getWarningController().continueExecution) {
                throw new FileNotFoundException("The operation was interrupted.");
            }
        }
//        if (decryptor.src.length())
    }
    public static String performEncryption(FileEncryptor encryptor) throws IOException {
        //Encrypting
        StringBuilder information = new StringBuilder();
        information.append("Encrypting..." + "\n");
        encryptor.encrypt();

        information.append(encryptor.counter).append(" files are encrypted, ").append(encryptor.ignoredCounter).append(" files ignored");
        return information.toString();
    }
    public static String performDecryption(FileDecryptor decryptor) throws IOException, IntegrityException {

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
