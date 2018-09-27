package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Random;

public class FileEncryptor {
	private static FileEncryptor encryptor = new FileEncryptor();

	boolean deleteOriginal;
	boolean integrityControl;
	File src, dst, keyFile;
	int counter, ignoredCounter;

	private FileEncryptor() {}
	public static FileEncryptor getEncryptor(File src, File dst, File keyFile, boolean deleteOriginal, boolean integrityControl) {
		encryptor.src = src;
		encryptor.dst = src;
		encryptor.keyFile = keyFile;
		encryptor.deleteOriginal = deleteOriginal;
		encryptor.integrityControl = integrityControl;
		encryptor.counter = 0;
		encryptor.ignoredCounter = 0;

		return encryptor;
	}

	public void encrypt() throws IOException {
		byte[] key = new byte[32];
//		TODO: rethrow higher
		try (InputStream is = new FileInputStream(keyFile)) {
			is.read(key);
		}
		final byte[] finalKey = FingerPrinter.addFingerPrint(key);
		Files.walkFileTree(src.toPath(), new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (!file.toFile().getName().substring(file.toFile().getName().lastIndexOf(".")+1).equals("ddiribas")) {
					counter++;
					copyEncrypted(file.toFile(), file.getParent().toFile(), finalKey);
					if(deleteOriginal) file.toFile().delete();
				} else {
					ignoredCounter++;
				}
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
				return FileVisitResult.CONTINUE;
			}
		});
	}

	public void copyEncrypted(File src, File dst, byte[] key) {
		dst = new File(dst.getPath().concat("/").concat(getRandomName(10, "ddiribas")));
		try (InputStream srcInputStream = new FileInputStream(src); OutputStream dstOutputStream = new FileOutputStream(dst)) {
			String name = src.getName();
			dstOutputStream.write(new byte[] { (byte) name.length() });
			dstOutputStream.write(stringToByte(name));

			byte[] buffer = new byte[srcInputStream.available()];
			srcInputStream.read(buffer, 0, srcInputStream.available());
			buffer = encryptBytes(buffer, key);
			dstOutputStream.write(buffer, 0, buffer.length);

			if (integrityControl) {
				addHash(name, buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addHash(String name, byte[] buffer) {
		final File bufferKeyFile = new File(keyFile.getParent(), "buffer.dkey");
		try (InputStream is = new FileInputStream(keyFile); OutputStream os = new FileOutputStream(bufferKeyFile)) {
			byte[] byte32 = new byte[32]; byte[] byte64;
			byte[] nameHash = StribogBouncy.getByteHash256(name.getBytes("UTF-8"));
			byte[] fileHash = StribogBouncy.getByteHash512(buffer);
			boolean found = false;

			is.read(byte32); os.write(byte32);
			while (is.available() > 0) {
				byte32 = new byte[32]; byte64 = new byte[64];
				is.read(byte32); os.write(byte32);
				if (Arrays.equals(byte32, nameHash)) {
					is.skip(64); os.write(fileHash);
					found = true;
				} else {
					is.read(byte64); os.write(byte64);
				}
			}
			if (!found) {
				os.write(nameHash); os.write(fileHash);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (InputStream is = new FileInputStream(bufferKeyFile) {
			@Override
			public void close() throws IOException {
				super.close();
				bufferKeyFile.delete();
			}
		}; OutputStream os = new FileOutputStream(keyFile)) {
			byte[] keyBuffer = new byte[is.available()];
			is.read(keyBuffer); os.write(keyBuffer);
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
}