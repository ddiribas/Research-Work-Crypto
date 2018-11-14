package ru.ddiribas;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.modes.G3413CFBBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Base64;
import ru.ddiribas.Encryption.KuznechikBouncy;

import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Scanner;

public class Test {
    private static final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine()), new PKCS7Padding());
    private static final SecureRandom random = new SecureRandom();
    private KeyParameter key;

    private byte[] processing(byte[] input, boolean encrypt) throws DataLengthException, InvalidCipherTextException {

        int blockSize = cipher.getBlockSize();
        int inputOffset = 0;
        int inputLength = input.length;
        int outputOffset = 0;

        byte[] iv = new byte[blockSize];
        if(encrypt) {
            random.nextBytes(iv);
            outputOffset += blockSize;
        } else {
            System.arraycopy(input, 0 , iv, 0, blockSize);
            inputOffset += blockSize;
            inputLength -= blockSize;
        }
        cipher.init(encrypt, new ParametersWithIV(key, iv));
        byte[] output = new byte[cipher.getOutputSize(inputLength) + outputOffset];

        if(encrypt) {
            System.arraycopy(iv, 0 , output, 0, blockSize);
        }

        int outputLength = outputOffset + cipher.processBytes(
                input, inputOffset, inputLength, output, outputOffset);

        outputLength += cipher.doFinal(output, outputLength);

        return Arrays.copyOf(output, outputLength);
    }
    private static byte[] encryptBytes(byte[] data, byte[] key) { // Encryption Algorithm is written into here
        KuznechikBouncy kuznechik = new KuznechikBouncy(key);
//		System.out.println("Длина выхода" + kuznechik.cipher.getOutputSize(data.length));
        try {
            data = kuznechik.byteEncrypt(data, key);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return data;
    }
    private static byte[] decryptBytes(byte[] data, byte[] key) { // Decryption Algorithm is written into here
        KuznechikBouncy kuznechik = new KuznechikBouncy(key);
        try {
            data = kuznechik.byteDecrypt(data, key);
        } catch (InvalidCipherTextException e) {
            e.printStackTrace();
        }
        return data;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
//        while (true) {
//            Scanner in = new Scanner(System.in);
//            File file = new File(in.nextLine());
//            System.out.println(file.getName().length());
//            System.out.println(cipher.getOutputSize(file.getName().length()));
//            System.out.println(Integer.MAX_VALUE);
//        }
        byte[] key = new byte[32];
        try (InputStream is = new FileInputStream(new File("C://Share/Диплом/KeyFile.dkey"))) {
            is.read(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String a = "123.txt";
        System.out.println(cipher.getOutputSize(a.length()));
        System.out.println(a);
        a = Base64.toBase64String(encryptBytes(a.getBytes(), key));
        System.out.println(a);
        a = URLEncoder.encode(a, "UTF-8");
        System.out.println(a);
        a = URLDecoder.decode(a, "UTF-8");
        System.out.println(a);
        a = new String(decryptBytes(Base64.decode(a), key));
        System.out.println(a);
    }
}
