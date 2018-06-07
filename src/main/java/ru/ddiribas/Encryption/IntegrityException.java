package ru.ddiribas.Encryption;

import java.io.File;

public class IntegrityException extends Exception {

	File[] integrityFailedFiles, hashNotFoundFiles;

	public IntegrityException(File[] integrityFailedFiles, File[] hashNotFoundFiles) {
		this.integrityFailedFiles = integrityFailedFiles;
		this.hashNotFoundFiles = hashNotFoundFiles;
	}

	@Override
	public String getLocalizedMessage() {
		StringBuilder result = new StringBuilder();
		if (hashNotFoundFiles.length != 0) {
			result = result.append("Hash not found for files (standard decryption applied): \n");
			for (File f : hashNotFoundFiles) {
				result.append(f.getName()).append("\n");
			}
		}
		if (integrityFailedFiles.length != 0) {
			result = result.append("Integrity broken for files: \n");
			for (File f : integrityFailedFiles) {
				result.append(f.getName()).append("\n");
			}
		}
		return result.toString();
	}
}
