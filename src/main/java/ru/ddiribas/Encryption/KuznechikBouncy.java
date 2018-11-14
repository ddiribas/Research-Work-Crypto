package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.G3413CFBBlockCipher;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.SecureRandom;
import java.util.Arrays;

public class KuznechikBouncy {

    private final BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new G3413CFBBlockCipher(new GOST3412_2015Engine()), new PKCS7Padding());
    private final SecureRandom random = new SecureRandom();
    private KeyParameter key;

    public KuznechikBouncy(byte[] key) {
        this.key = new KeyParameter(key);
    }

    public byte[] byteEncrypt(byte[] plainText, byte[] key) throws InvalidCipherTextException {
        return processing(plainText, true);
    }
    public byte[] byteDecrypt(byte[] cipherText, byte[] key) throws InvalidCipherTextException {
        return processing(cipherText, false);
    }

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
}
