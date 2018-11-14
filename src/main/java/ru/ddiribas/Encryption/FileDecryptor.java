package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.util.encoders.Base64;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class FileDecryptor {
	private static FileDecryptor decryptor = new FileDecryptor();

	private boolean deleteOriginal;
	private boolean integrityControl;
	private boolean fingerPrint;
	private boolean encryptName;

	File src, dst, keyFile;
	int counter, ignoredCounter;
	private List<File> integrityFailedFiles = new LinkedList<>();
	private List<File> hashNotFoundFiles = new LinkedList<>();

	private FileDecryptor() {}
	public static FileDecryptor getDecryptor(File src, File dst, File keyFile, boolean deleteOriginal, boolean integrityControl, boolean fingerPrint, boolean encryptName) {
		decryptor.src = src;
		decryptor.dst = dst;
		decryptor.keyFile = keyFile;
		decryptor.deleteOriginal = deleteOriginal;
		decryptor.integrityControl = integrityControl;
		decryptor.fingerPrint = fingerPrint;
		decryptor.encryptName = encryptName;
		decryptor.counter = 0;
		decryptor.ignoredCounter = 0;

		return decryptor;
	}
	
	void decrypt() throws IOException, IntegrityException {
		byte[] key = new byte[32];
		final byte[] finalKey;
		try (InputStream is = new FileInputStream(keyFile)) {
			is.read(key);
		}

		if (fingerPrint)
			finalKey = FingerPrintAuthenticator.addFingerPrint(key);
		else
			finalKey = key;

		Files.walkFileTree(src.toPath(), new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (file.toFile().getName().substring(file.toFile().getName().lastIndexOf(".")+1).equals("ddiribas")) {
					counter++;
					copyDecrypted(file.toFile(), file.getParent().toFile(), finalKey);
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
		if (!hashNotFoundFiles.isEmpty() || !integrityFailedFiles.isEmpty()) {
			File[] tempIFF = integrityFailedFiles.toArray(new File[0]);
			File[] tempHNFF = hashNotFoundFiles.toArray(new File[0]);
			integrityFailedFiles.clear();
			hashNotFoundFiles.clear();
			throw new IntegrityException(tempIFF, tempHNFF);
		}
//		if (src.isFile()) {
//            if (src.getName().substring(src.getName().lastIndexOf(".")+1).equals("ddiribas")) {
//                counter++;
//            	copyDecrypted(src, dst, key);
//                if (deleteOriginal) src.delete();
//            } else {
//				ignoredCounter++;
//            }
//        } else {
////			src.renameTo(new File(new String(decryptBytes(src.getName().getBytes("UTF-8"), key), "UTF-8")));
//            File[] files = src.listFiles();
//
//            for (File f : files) {
//                decrypt(f, src, key);
//            }
//        }
	}

	private void copyDecrypted(File src, File dst, byte[] key) {
        try (InputStream is = new FileInputStream(src)) {
			byte[] buffer = new byte[is.available()];
			is.read(buffer, 0, is.available());
			if (integrityControl) {
				switch (checkIntegrity(src.getName(), buffer)) {
					case SUCCESS: { break; }
					case HASH_NOT_FOUND: { hashNotFoundFiles.add(src); break; }
					case INTEGRITY_BROKEN: { integrityFailedFiles.add(src);return; }
				}
			}
			String fileName;
			if (encryptName){
				fileName = decryptFileName(src.getName(), key);
			} else {
				fileName = src.getName().substring(0, src.getName().lastIndexOf("."));
			}
			try (OutputStream os = new FileOutputStream(dst.getPath().concat("/").concat(fileName))) {
                buffer = decryptBytes(buffer, key);
                os.write(buffer, 0, buffer.length);
            }
        } catch (IOException e) {
        	e.printStackTrace();
		}
	}
	private String decryptFileName(String fileName, byte[] key) throws UnsupportedEncodingException {
//		System.out.println(fileName);
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
//		System.out.println(fileName);
		fileName = URLDecoder.decode(fileName, "UTF-8");
//		System.out.println(fileName);
		fileName = new String(decryptBytes(Base64.decode(fileName), key));
//		System.out.println(fileName);
		return fileName;
	}

	private IntegrityCheckResult checkIntegrity(String fileName, byte[] buffer) {
		System.out.println(fileName);
		try (InputStream is = new FileInputStream(keyFile)) {
			byte[] byte32; byte[] byte64;
			byte[] nameHash = StribogBouncy.getByteHash256(fileName.getBytes(StandardCharsets.UTF_8));
			byte[] fileHash = StribogBouncy.getByteHash512(buffer);

			is.skip(32);
			while (is.available() > 0) {
				byte32 = new byte[32]; byte64 = new byte[64];
				is.read(byte32);
				if (Arrays.equals(byte32, nameHash)) {
					is.read(byte64);
					if (Arrays.equals(byte64, fileHash)) {
						return IntegrityCheckResult.SUCCESS;
					} else {
						return IntegrityCheckResult.INTEGRITY_BROKEN;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return IntegrityCheckResult.HASH_NOT_FOUND;
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
}