package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

public class FileEncryptor {
	private static FileEncryptor encryptor = new FileEncryptor();

	private static boolean deleteOriginal;
	private static boolean integrityControl;
	static int counter;
	
	private FileEncryptor() {}
	public static FileEncryptor getEncryptor(boolean deleteOriginal, boolean integrityControl) {
		FileEncryptor.deleteOriginal = deleteOriginal;
		FileEncryptor.integrityControl = integrityControl;
		counter = 0;

		return encryptor;
	}

	public void encrypt(File src, File dst, byte[] key) {
		if (src.isFile()) {
			if (!src.getName().substring(src.getName().lastIndexOf(".")+1).equals("ddiribas")) {
				counter++;
				copyEncrypted(src, dst, key);
				if(deleteOriginal) src.delete();
			} else {
				System.out.println("File is already encrypted");
			}
		} else {
//			File file = new File(src.getParent() + "/" + Base64.encode(encryptBytes(src.getName().getBytes("UTF-8"), key)));
//			src.renameTo(file);
//			src = file;
			File[] files = src.listFiles();

			for (File f : files) {
				encrypt(f, src, key);
			}
		}
	}

	public void copyEncrypted(File source, File dest, byte[] key) {

		dest = new File(dest.getPath().concat("/").concat(getRandomName(10, "ddiribas")));

		try (InputStream is = new FileInputStream(source); OutputStream os = new FileOutputStream(dest)) {
			os.write(new byte[] { (byte) source.getName().length() });
			os.write(stringToByte(source.getName()));

			byte[] buffer = new byte[is.available()];
			is.read(buffer, 0, is.available());

			buffer = encryptBytes(buffer, key);
			os.write(buffer, 0, buffer.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] encryptBytes(byte[] data, byte[] key) { // Encryption Algorithm is written into here
		KuznechikBouncy kuznechik = new KuznechikBouncy(key);
        try {
            data = kuznechik.byteEncrypt(data, key);
        } catch (InvalidCipherTextException e) {
        	e.printStackTrace();
        }
        return data;
    }

	public byte[] stringToByte(String data) {
		char[] ca = data.toCharArray();
		byte[] res = new byte[ca.length * 2]; // Character.BYTES = 2;

		for (int i = 0; i < res.length; i++) {
			res[i] = (byte) ((ca[i / 2] >> (8 - (i % 2) * 8)) & 0xff);
		}

		return res;
	}

	public String getRandomName(int length, String extend) {
		Random r = new Random();
		StringBuilder res = new StringBuilder();

		for (int i = 0; i < length; i++) {

			char c = 'a';
			int width = 'z' - 'a';

			if (r.nextInt(3) == 0) {
				c = 'A';
				width = 'Z' - 'A';
			}
			if (r.nextInt(3) == 1) {
				c = '0';
				width = '9' - '0';
			}

			res.append((char) (c + r.nextInt(width)));
		}

		res.append(".").append(extend);

		return res.toString();
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