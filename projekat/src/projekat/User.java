package projekat;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;

public class User {
	private String username;
	private String password;
	
	private PGPPublicKeyRingCollection publicKeyRingCollection;
	private PGPSecretKeyRingCollection secretKeyRingCollection;
	
	private File publicKeyRingDirectory;
    private File secretKeyRingDirectory;

	public File getPublicKeyRingDirectory() {
		return publicKeyRingDirectory;
	}

	public void setPublicKeyRingDirectory(File publicKeyRingDirectory) {
		this.publicKeyRingDirectory = publicKeyRingDirectory;
	}

	public File getSecretKeyRingDirectory() {
		return secretKeyRingDirectory;
	}

	public void setSecretKeyRingDirectory(File secretKeyRingDirectory) {
		this.secretKeyRingDirectory = secretKeyRingDirectory;
	}

	public PGPPublicKeyRingCollection getPublicKeyRingCollection() {
		return publicKeyRingCollection;
	}

	public void setPublicKeyRingCollection(PGPPublicKeyRingCollection publicKeyRingCollection) {
		this.publicKeyRingCollection = publicKeyRingCollection;
	}

	public PGPSecretKeyRingCollection getSecretKeyRingCollection() {
		return secretKeyRingCollection;
	}

	public void setSecretKeyRingCollection(PGPSecretKeyRingCollection secretKeyRingCollection) {
		this.secretKeyRingCollection = secretKeyRingCollection;
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
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + "]";
	}

}
