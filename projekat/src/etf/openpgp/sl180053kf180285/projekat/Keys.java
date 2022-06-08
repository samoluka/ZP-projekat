package etf.openpgp.sl180053kf180285.projekat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.operator.bc.BcKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import etf.openpgp.sl180053kf180285.util.Pair;

/**
 * Klasa za rad sa kljucevima, ukljucuje uvoz i izvoz javnih i privatnih kljueva
 * trenutno ulogovanog korisnika, dohvatanje javnih i privatnih kljuceva, kao i
 * njihovu pretragu po ID.Takodje omogucava brisanje para kljuceva.
 *
 */

public class Keys {

	/**
	 * dohvata kljuceve korisnika
	 */
	public Iterator<PGPSecretKeyRing> getSecretRings(User u) {
		return u.getSecretKeyRingCollection().getKeyRings();
	}

	/**
	 * dohvata uvezene javne kljuceve korisnika
	 */
	public Iterator<PGPPublicKeyRing> getImportedPublicRings(User u) {
		return u.getImportedPublicKeyRingCollection().getKeyRings();
	}

	/**
	 * dohvata par korisnickih kljuceva po ID kljuca
	 */
	public Pair<PGPPublicKey, PGPPublicKey> findPublicRing(long keyID, User u) {
		Iterator<PGPSecretKeyRing> pkrIterator = getSecretRings(u);
		int found = 0;
		while (pkrIterator.hasNext() && found == 0) {
			PGPSecretKeyRing secretRing = pkrIterator.next();
			if (keyID == secretRing.getPublicKey().getKeyID()) {
				Iterator<PGPPublicKey> iter = secretRing.getPublicKeys();
				return new Pair(iter.next(), iter.next());
			}
		}
		return null;
	}

	/**
	 * dohvata par uvezenih javnih kljuceva korisnika po ID kljuca
	 */
	public Pair<PGPPublicKey, PGPPublicKey> findPublicRingWithImported(long keyID, User u) {
		Iterator<PGPSecretKeyRing> pkrIterator = getSecretRings(u);
		while (pkrIterator.hasNext()) {
			PGPSecretKeyRing secretRing = pkrIterator.next();
			if (keyID == secretRing.getPublicKey().getKeyID()) {
				Iterator<PGPPublicKey> iter = secretRing.getPublicKeys();
				return new Pair<PGPPublicKey, PGPPublicKey>(iter.next(), iter.next());
			}
		}
		Iterator<PGPPublicKeyRing> publicIter = getImportedPublicRings(u);
		while (publicIter.hasNext()) {
			PGPPublicKeyRing publicRing = publicIter.next();
			if (keyID == publicRing.getPublicKey().getKeyID()) {
				Iterator<PGPPublicKey> iter = publicRing.getPublicKeys();
				return new Pair<PGPPublicKey, PGPPublicKey>(iter.next(), iter.next());
			}
		}
		return null;
	}

	/**
	 * dohvata kljuc korisnika po ID kljuca
	 */
	public PGPSecretKeyRing findPrivateRing(long keyID, User u) {
		Iterator<PGPSecretKeyRing> pkrIterator = getSecretRings(u);
		PGPSecretKeyRing privateKeyRing = null;
		int found = 0;
		while (pkrIterator.hasNext() && found == 0) {
			privateKeyRing = pkrIterator.next();
			Iterator<PGPSecretKey> iterKey = privateKeyRing.getSecretKeys();
			while (iterKey.hasNext()) {
				PGPSecretKey privateKey = iterKey.next();
				if (keyID == privateKey.getKeyID()) {
					found = 1;
					break;
				}
			}
		}
		if (found == 1) {
			return privateKeyRing;
		}
		return null;
	}

	/**
	 * izvoz izabranog javnog kljuca na zadatu putanju
	 */
	public void publicKeyExport(String savePath, long keyID, User u) throws IOException {
		Pair<PGPPublicKey, PGPPublicKey> publicKeys = findPublicRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		publicKeys.getFirst().encode(aos);
		publicKeys.getSecond().encode(aos);
		aos.close();
	}

	/**
	 * uvoz izabranog javnog kljuca sa zadate putanje
	 */
	public void publicKeyImport(String importPath, User u) throws IOException, PGPException {
		PGPPublicKeyRingCollection currentPKRC = u.getImportedPublicKeyRingCollection();
		File currentPKRD = u.getImportedPublicKeyRingDirectory();

		ArmoredInputStream ais = new ArmoredInputStream(new FileInputStream(new File(importPath)));
		PGPPublicKeyRingCollection added = new PGPPublicKeyRingCollection(ais, new BcKeyFingerprintCalculator());
		Iterator<PGPPublicKeyRing> ringIterator = added.getKeyRings();

		while (ringIterator.hasNext()) {
			PGPPublicKeyRing addedRing = ringIterator.next();
			currentPKRC = PGPPublicKeyRingCollection.addPublicKeyRing(currentPKRC, addedRing);
			ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(currentPKRD));
			currentPKRC.encode(aos);
			aos.close();
		}

		u.setImportedPublicKeyRingCollection(currentPKRC);
	}

	/**
	 * izvoz izabranog privatnog kljuca na zadatu putanju
	 */
	public void privateKeyExport(String savePath, long keyID, User u) throws IOException {
		PGPSecretKeyRing privateKeyRing = findPrivateRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		privateKeyRing.encode(aos);
		aos.close();
	}

	/**
	 * uvoz izabranog javnog kljuca sa zadate putanje
	 */
	public void secretKeyImport(String importPath, User u) throws IOException, PGPException {
		PGPSecretKeyRingCollection currentSKRC = UserProvider.getInstance().getCurrentUser()
				.getSecretKeyRingCollection();
		File currentSKRD = UserProvider.getInstance().getCurrentUser().getSecretKeyRingDirectory();

		ArmoredInputStream ais = new ArmoredInputStream(new FileInputStream(new File(importPath)));
		PGPSecretKeyRingCollection added = new PGPSecretKeyRingCollection(ais, new BcKeyFingerprintCalculator());
		Iterator<PGPSecretKeyRing> ringIterator = added.getKeyRings();

		while (ringIterator.hasNext()) {
			PGPSecretKeyRing addedRing = ringIterator.next();
			currentSKRC = PGPSecretKeyRingCollection.addSecretKeyRing(currentSKRC, addedRing);
			ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(currentSKRD));
			currentSKRC.encode(aos);
			aos.close();
		}

		u.setSecretKeyRingCollection(currentSKRC);
	}

	/**
	 * klasa za brisanje para kljuceva, zahteva sifru pod kojom se cuva privatni
	 * kljuc
	 */
	public boolean deleteSecretKey(long keyID, String password, User user) throws IOException {
		PGPSecretKeyRingCollection SKRC = user.getSecretKeyRingCollection();
		PGPSecretKeyRing secretRing = findPrivateRing(keyID, user);

		File SCRD = user.getSecretKeyRingDirectory();

		try {
			secretRing.getSecretKey().extractPrivateKey(
					new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));
		} catch (PGPException e) {
			// e.printStackTrace();
			return false;
		}
		SKRC = PGPSecretKeyRingCollection.removeSecretKeyRing(SKRC, secretRing);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(SCRD));
		SKRC.encode(aos);
		aos.close();

		UserProvider.getInstance().getCurrentUser().setSecretKeyRingCollection(SKRC);
		// deletePublicKey(keyID, user);

		return true;
	}

}
