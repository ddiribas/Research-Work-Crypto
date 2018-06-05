package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDecryptor {
	private static FileDecryptor decryptor = new FileDecryptor();

	private static boolean deleteOriginal;
	private static boolean integrityControl;
	static int counter;
	static int ignoredCounter;

	private FileDecryptor() {}
	public static FileDecryptor getDecryptor(boolean deleteOriginal, boolean integrityControl) {
		FileDecryptor.deleteOriginal = deleteOriginal;
		FileDecryptor.integrityControl = integrityControl;
		counter = 0;
		ignoredCounter = 0;

		return decryptor;
	}
	
	public void decrypt(File src, File dst, byte[] key) {
		if (src.isFile()) {
            if (src.getName().substring(src.getName().lastIndexOf(".")+1).equals("ddiribas")) {
                counter++;
            	copyDecrypted(src, dst, key);
                if (deleteOriginal) src.delete();
            } else {
				ignoredCounter++;
            }
        } else {
//			src.renameTo(new File(new String(decryptBytes(src.getName().getBytes("UTF-8"), key), "UTF-8")));
            File[] files = src.listFiles();

            for (File f : files) {
                decrypt(f, src, key);
            }
        }
	}

	public void copyDecrypted(File source, File dest, byte[] key) {
        try (InputStream is = new FileInputStream(source)) {
            byte[] name = new byte[is.read() * 2];
            is.read(name);
            String fileName = bytesToString(name);

            try (OutputStream os = new FileOutputStream(dest.getPath().concat("/").concat(fileName))) {
                byte[] buffer = new byte[is.available()];
                is.read(buffer, 0, is.available());

                buffer = decryptBytes(buffer, key);
                os.write(buffer, 0, buffer.length);
            }
        } catch (IOException e) {
        	e.printStackTrace();
		}
	}

	public String bytesToString(byte[] data) {
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < data.length / 2; i++) {
			char c = (char) ((data[i * 2] << 8) | data[i * 2 + 1]);
			res.append(c);
		}

		return res.toString();
	}

	private byte[] decryptBytes(byte[] data, byte[] key) { // Decryption Algorithm is written into here
		KuznechikBouncy kuznechik = new KuznechikBouncy(key);
		try {
			data = kuznechik.byteDecrypt(data, key);
		} catch (InvalidCipherTextException e) {
			e.printStackTrace();
		}
		return data;
	}

	public void copy(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;

		try {
			dest = new File(dest.getPath().concat("/").concat(source.getName()));

			is = new FileInputStream(source);
			os = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			int tl = 0;

			while ((length = is.read(buffer)) > 0) {
				tl += length;
				os.write(buffer, 0, length);
			}

			System.out.println(tl + " bytes");
		} finally {
			is.close();
			os.close();
		}
	}
}