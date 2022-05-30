package projekat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class Keys {

	// ako se zove secret nek se i metoda tako zove cisto eto
	public Iterator<PGPSecretKeyRing> getSecretRings(User u) {
		return u.getSecretKeyRingCollection().getKeyRings();
	}

	public Iterator<PGPPublicKeyRing> getPublicRings(User u) {
		return u.getPublicKeyRingCollection().getKeyRings();
	}

	// gornje 2 metode vracaju prstenove kroz koje se iterira i hvata kljuc iz njih
	// na klik dugmeta sledeci/prethodni pozivace se odgovarajuca metoda
	// kod njih je implementirano tako da se na klik dugmeta sledeci sa + argumentom
	// prelazi na sledeci javni/privatni kljuc(u zavisnosti kroz koje kljuceve zelis
	// da iteriras)

	// npr prikaz javnih kljuceva, analogno je za privatne
	// kod njih metoda getPublicRing pomocu indeksa vraca odgovarajuci prsten
	// indeks krece od 0 a sa +/- se povecava i dohavata odgovarajuci prsten iz
	// koga se sa getPublicKeys dohvata iterator kljuceva(u stvari postoji 1 kljuc
	// po prstenu i on se sa
	// iterator.next() dohvata, a potom se ispisuju one 3 vrednosti koje smo i dosad
	// ispisivali)

	// mozda da bi se razlikovalo od njih stavis da dugme sledeci salje int umjesto
	// ovog char
	// ali sustina je svakako ista

	// znaci u tvojoj klasi ti isto treba jedan currentIndex da bi se znalo koji
	// kljuc treba da showPublicKey/PrivateKey
	// prikazuje

	// razlika sto ovde dohvata ring jedan po jedan, a kod nas ces ih dohatit
	// odjednom pa iterirat u svojoj klasi kroz njih
	// i kljuceve

	/*
	 * public void showPublicKey(char sign) { PGPPublicKeyRing oldRing =
	 * currentPublicRing; if (sign == '+') { currentPublicIndex++; currentPublicRing
	 * = keyGenerator.getPublicRing(currentPublicIndex); if (currentPublicRing ==
	 * null) { currentPublicRing = oldRing; currentPublicIndex--; } } else {
	 * currentPublicIndex--; currentPublicRing =
	 * keyGenerator.getPublicRing(currentPublicIndex); if (currentPublicRing ==
	 * null) { currentPublicRing = oldRing; currentPublicIndex++; } }
	 * 
	 * if (currentPublicRing != null) { java.util.Iterator<PGPPublicKey> iterPrivate
	 * = currentPublicRing.getPublicKeys(); PGPPublicKey currentPublicKey =
	 * iterPrivate.next();
	 * publicKeyInfo[0][1].setText(String.valueOf(currentPublicKey.getKeyID()));
	 * publicKeyInfo[1][1].setText(currentPublicKey.getUserIDs().next());
	 * publicKeyInfo[2][1].setText(String.valueOf(currentPublicKey.getCreationTime()
	 * )); } }
	 */

	public PGPPublicKeyRing findPublicRing(long keyID, User u) {
		Iterator<PGPPublicKeyRing> pkrIterator = getPublicRings(u);
		PGPPublicKeyRing publicKeyRing = null;
		int found = 0;
		while (pkrIterator.hasNext() && found == 0) {
			publicKeyRing = pkrIterator.next();
			Iterator<PGPPublicKey> iterKey = publicKeyRing.getPublicKeys();
			while (iterKey.hasNext()) {
				PGPPublicKey publicKey = iterKey.next();
				if (keyID == publicKey.getKeyID()) {
					found = 1;
					break;
				}
			}
		}
		if (found == 1) {
			return publicKeyRing;
		}
		return null;
	}

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

	public void publicKeyExport(String savePath, long keyID, User u) throws IOException {
		PGPPublicKeyRing publicKeyRing = findPublicRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		publicKeyRing.encode(aos);
		aos.close();
	}
	
	public void publicKeyImport(String importPath) throws IOException, PGPException {
		PGPPublicKeyRingCollection currentPKRC = UserProvider.getInstance().getCurrentUser()
				.getPublicKeyRingCollection();
		File currentPKRD = UserProvider.getInstance().getCurrentUser().getPublicKeyRingDirectory();

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
	}

	// *******dodati pitanje za sifru privatnog kljuca
	public void privateKeyExport(String savePath, long keyID, User u) throws IOException {
		PGPSecretKeyRing privateKeyRing = findPrivateRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		privateKeyRing.encode(aos);
		aos.close();
	}
	
	public void privateKeyImport(String importPath) throws IOException, PGPException {
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
	}

}
