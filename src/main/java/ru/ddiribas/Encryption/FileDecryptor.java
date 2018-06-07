package ru.ddiribas.Encryption;

import org.bouncycastle.crypto.InvalidCipherTextException;
import ru.ddiribas.MainApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

	boolean deleteOriginal;
	boolean integrityControl;

	File src, dst, keyFile;
	int counter, ignoredCounter;
	List<File> integrityFailedFiles = new LinkedList<>();
	List<File> hashNotFoundFiles = new LinkedList<>();

	private FileDecryptor() {}
	public static FileDecryptor getDecryptor(File src, File dst, File keyFile, boolean deleteOriginal, boolean integrityControl) {
		decryptor.src = src;
		decryptor.dst = src;
		decryptor.keyFile = keyFile;
		decryptor.deleteOriginal = deleteOriginal;
		decryptor.integrityControl = integrityControl;
		decryptor.counter = 0;
		decryptor.ignoredCounter = 0;

		return decryptor;
	}
	
	public void decrypt() throws IOException, IntegrityException {
		final byte[] key = new byte[32];
//		TODO: rethrow higher
		try (InputStream is = new FileInputStream(keyFile)) {
			is.read(key);
		}
		Files.walkFileTree(src.toPath(), new FileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
				if (file.toFile().getName().substring(file.toFile().getName().lastIndexOf(".")+1).equals("ddiribas")) {
					counter++;
					copyDecrypted(file.toFile(), file.getParent().toFile(), key);
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
			throw new IntegrityException(integrityFailedFiles.toArray(new File[integrityFailedFiles.size()]), hashNotFoundFiles.toArray(new File[hashNotFoundFiles.size()]));
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

	public void copyDecrypted(File src, File dst, byte[] key) {
        try (InputStream is = new FileInputStream(src)) {
            byte[] name = new byte[is.read() * 2];
            is.read(name);
            String fileName = bytesToString(name);
			byte[] buffer = new byte[is.available()];
			is.read(buffer, 0, is.available());
			if (integrityControl) {
				switch (checkIntegrity(fileName, buffer)) {
					case SUCCESS: { break; }
					case HASH_NOT_FOUND: { hashNotFoundFiles.add(src); break; }
					case INTEGRITY_BROKEN: { integrityFailedFiles.add(src);return; }
				}
			}
			try (OutputStream os = new FileOutputStream(dst.getPath().concat("/").concat(fileName))) {
                buffer = decryptBytes(buffer, key);
                os.write(buffer, 0, buffer.length);
            }
        } catch (IOException e) {
        	e.printStackTrace();
		}
	}

	private IntegrityCheckResult checkIntegrity(String fileName, byte[] buffer) {
		try (InputStream is = new FileInputStream(keyFile)) {
			byte[] byte32; byte[] byte64;
			byte[] nameHash = StribogBouncy.getByteHash256(fileName.getBytes("UTF-8"));
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
}