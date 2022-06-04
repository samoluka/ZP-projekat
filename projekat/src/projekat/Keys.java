package projekat;

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

import util.Pair;

public class Keys {

	// ako se zove secret nek se i metoda tako zove cisto eto
	public Iterator<PGPSecretKeyRing> getSecretRings(User u) {
		return u.getSecretKeyRingCollection().getKeyRings();
	}

	public Iterator<PGPPublicKeyRing> getImportedPublicRings(User u) {
		return u.getImportedPublicKeyRingCollection().getKeyRings();
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
		Pair<PGPPublicKey, PGPPublicKey> publicKeys = findPublicRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		publicKeys.getFirst().encode(aos);
		publicKeys.getSecond().encode(aos);
		aos.close();
	}

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

	// *******dodati pitanje za sifru privatnog kljuca
	public void privateKeyExport(String savePath, long keyID, User u) throws IOException {
		PGPSecretKeyRing privateKeyRing = findPrivateRing(keyID, u);
		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
		privateKeyRing.encode(aos);
		aos.close();
	}

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

	// metoda za brisanje kljuceva
	// oni su radili tako da javni kljuc moze da se obrise uvijek, a da kad se brise
	// privatni automatski se obrise i njegov odgovarajuci javni
	// prvi dio mi ima smisla, jer u sustini svoj javni kljuc uvijek mozes da
	// obrises jer ces i dalje moci da nesto sto je njim sifrovano
	// od strane drugih korisnika desifrujes sa odgovarajucim privatnim kljucem, a i
	// tudji javni svakako mozes uvijek da obrises
	// samo sto neces vise moci njim da sifrujes
	// nisam siguran da li kod brisanja privatnog kljuca da automatski obrisemo i
	// javni zato sto je razlika u odnosu na njihov projekat
	// to sto se kod nas i ukoliko korisnik obrise svoj privatni i dalje ostali
	// korisnici mogu imati odgovarajuci javni kljuc tog privatnog
	// kljuca i mogu da kriptuju poruku sa njime (u sustini mi bismo onda morali da
	// iteriramo kroz prstenove svih korisnika i da brisemo
	// taj javni kljuc, ali mislim da to nije u okviru ovog projekta vec u okviru
	// povlacenja janih kljuceva - poslednji slajd PGP prezentacije)
	// znaci kod njih je to ok jer nakon tog brisanja jednog privatnog vise ne moze
	// niko da sifruje sa odgovarajucim javnim

	// e sad, posto se u postavci naglasava generisanje i brisanje PARA kljuceva
	// mozda je ipak bolje da je uradjeno kao i kod anje, s tim sto cemo pri
	// eventualnom testiranju izbjegavati da sifrujemo
	// necijim javnim ako znamo da je njegov odgovarajuci privatni kljuc obrisan

	// znaci na kraju
	// pri brisanju javnog kljuca ostavicemo odgovarajuci privatni, dok cemo pri
	// brisanju privatnog obrisati i njegov odgovarajuci javni

	// u gui dijelu za pregled kljuceva moze se dodati dugme obrisi
	// pri kliku na dugme obrisi kod javnog kljuca, metodu deletePublicKey zvati sa
	// id kljuca i objektom trenutnog usera
	// a pri kliku na dugme obrisi kod privatnog kljuca, prvo pitaj za sifru od tog
	// kljuca i zatim pozovi metodi
	// deletePrivateKey sa id kljuca, unesenom sifrom i objektom trenutnog usera
	// ukoliko se unese pogresna sifra baci se PGPException i vraca se false
	// brisanje javnog kljuca se uvijek uspijesno izvrsi

	public boolean deleteSecretKey(long keyID, String password, User user) throws IOException {
		PGPSecretKeyRingCollection SKRC = user.getSecretKeyRingCollection();
		PGPSecretKeyRing secretRing = findPrivateRing(keyID, user);

		File SCRD = user.getSecretKeyRingDirectory();

		try {
			secretRing.getSecretKey().extractPrivateKey(
					new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));
		} catch (PGPException e) {
//			e.printStackTrace();
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

//	public void deletePublicKey(long keyID, User user) throws IOException {
//		PGPPublicKeyRingCollection PCRC = user.getPublicKeyRingCollection();
//		File PCRD = user.getPublicKeyRingDirectory();
//		PGPPublicKeyRing publicRing = findPublicRing(keyID, user);
//
//		PCRC = PGPPublicKeyRingCollection.removePublicKeyRing(PCRC, publicRing);
//		ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(PCRD));
//		PCRC.encode(aos);
//		aos.close();
//
//		UserProvider.getInstance().getCurrentUser().setPublicKeyRingCollection(PCRC);
//		// initialize(path); zasad nek stoji, posle brisemo ako ne valja
//	}
}
