package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public class FileEncryptor {
	private static FileEncryptor encryptor = new FileEncryptor();

	private boolean deleteOriginal;
	private boolean integrityControl;
	private boolean fingerPrint;
	private boolean encryptName;
	private boolean passwordAuth;
	File src, dst, keyFile;
	int counter, ignoredCounter;

	private FileEncryptor() {}
	public static FileEncryptor getEncryptor(File src, File dst, File keyFile, boolean deleteOriginal, boolean integrityControl, boolean fingerPrint, boolean encryptName, boolean passwordAuth) {
		encryptor.src = src;
		encryptor.dst = dst;
		encryptor.keyFile = keyFile;
		encryptor.deleteOriginal = deleteOriginal;
		encryptor.integrityControl = integrityControl;
		encryptor.fingerPrint = fingerPrint;
		encryptor.encryptName = encryptName;
		encryptor.passwordAuth = passwordAuth;
		encryptor.counter = 0;
		encryptor.ignoredCounter = 0;

		return encryptor;
	}

	void encrypt() throws IOException {
		byte[] key = new byte[32];
		final byte[] finalKey;
		try (InputStream is = new FileInputStream(keyFile)) {
			is.read(key);
		}
		if (fingerPrint)
			key = FingerPrintAuthenticator.addFingerPrint(key);
		if (passwordAuth)
			key = PasswordAuthenticator.addPasswordPrint(key);
		finalKey = key;

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
					if (deleteOriginal) file.toFile().delete();
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

	private void copyEncrypted(File src, File dst, byte[] key) {
		if (encryptName) {
			try {
				dst = new File(dst.getPath(), encryptFileName(src.getName(), key));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else dst = new File (dst.getPath(), src.getName() + ".ddiribas");
		try (InputStream srcInputStream = new FileInputStream(src); OutputStream dstOutputStream = new FileOutputStream(dst)) {
			String name = dst.getName();

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
			byte[] nameHash = StribogBouncy.getByteHash256(name.getBytes(StandardCharsets.UTF_8));
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

    private String encryptFileName(String fileName, byte[] key) throws UnsupportedEncodingException {
//		System.out.println(fileName);
		fileName = Base64.toBase64String(encryptBytes(fileName.getBytes(), key));
//		System.out.println(fileName);
		fileName = URLEncoder.encode(fileName, "UTF-8");
//		System.out.println(fileName);
		return fileName + ".ddiribas";
	}

}