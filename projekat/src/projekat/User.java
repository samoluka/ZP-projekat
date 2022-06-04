package projekat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;

public class User {
	private String username;
	private String password;

	private PGPSecretKeyRingCollection secretKeyRingCollection;
	private PGPPublicKeyRingCollection importedPublicKeyRingCollection;

	private File secretKeyRingDirectory;
	private File importedPublicKeyRingDirectory;

	public File getSecretKeyRingDirectory() {
		return secretKeyRingDirectory;
	}

	public void setSecretKeyRingDirectory(File secretKeyRingDirectory) {
		this.secretKeyRingDirectory = secretKeyRingDirectory;
	}

	public PGPSecretKeyRingCollection getSecretKeyRingCollection() {
		return secretKeyRingCollection;
	}

	public void setSecretKeyRingCollection(PGPSecretKeyRingCollection secretKeyRingCollection) {
		this.secretKeyRingCollection = secretKeyRingCollection;
	}

	public PGPPublicKeyRingCollection getImportedPublicKeyRingCollection() {
		return importedPublicKeyRingCollection;
	}

	public void setImportedPublicKeyRingCollection(PGPPublicKeyRingCollection importedPublicKeyRingCollection) {
		this.importedPublicKeyRingCollection = importedPublicKeyRingCollection;
	}

	public File getImportedPublicKeyRingDirectory() {
		return importedPublicKeyRingDirectory;
	}

	public void setImportedPublicKeyRingDirectory(File importedPublicKeyRingDirectory) {
		this.importedPublicKeyRingDirectory = importedPublicKeyRingDirectory;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = username + ThreadLocalRandom.current().nextInt(10000, 99999 + 1) + HashText.getSHA(password);

		String path = System.getProperty("user.dir") + "/keyRingCollections/" + username;
		(new File(path)).mkdirs();

		try {
			secretKeyRingDirectory = new File(path + "\\privateKeyRing.asc");
			secretKeyRingDirectory.createNewFile();
			importedPublicKeyRingDirectory = new File(path + "\\importedPublicKeyRing.asc");
			importedPublicKeyRingDirectory.createNewFile();
			secretKeyRingCollection = new PGPSecretKeyRingCollection(
					new ArmoredInputStream(new FileInputStream(secretKeyRingDirectory)),
					new BcKeyFingerprintCalculator());
			importedPublicKeyRingCollection = new PGPPublicKeyRingCollection(
					new ArmoredInputStream(new FileInputStream(importedPublicKeyRingDirectory)),
					new BcKeyFingerprintCalculator());
		} catch (IOException | PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}

}
