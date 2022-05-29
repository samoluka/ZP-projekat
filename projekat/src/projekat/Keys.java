package projekat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

public class Keys {
	
	public Iterator<PGPSecretKeyRing> getPrivateRings() {
		return UserProvider.getInstance().getCurrentUser().getSecretKeyRingCollection().getKeyRings();
	}
	
	public Iterator<PGPPublicKeyRing> getPublicRings() {
		return UserProvider.getInstance().getCurrentUser().getPublicKeyRingCollection().getKeyRings();
	}
	
	
	// gornje 2 metode vracaju prstenove kroz koje se iterira i hvata kljuc iz njih
	// na klik dugmeta sledeci/prethodni pozivace se odgovarajuca metoda
	// kod njih je implementirano tako da se na klik dugmeta sledeci sa + argumentom
	// prelazi na sledeci javni/privatni kljuc(u zavisnosti kroz koje kljuceve zelis da iteriras)
	
	// npr prikaz javnih kljuceva, analogno je za privatne
	// kod njih metoda getPublicRing pomocu indeksa vraca odgovarajuci prsten
	// indeks krece od 0 a sa +/- se povecava i dohavata odgovarajuci prsten iz
	// koga se sa getPublicKeys dohvata iterator kljuceva(u stvari postoji 1 kljuc po prstenu i on se sa
	// iterator.next() dohvata, a potom se ispisuju one 3 vrednosti koje smo i dosad ispisivali)
	
	// mozda da bi se razlikovalo od njih stavis da dugme sledeci salje int umjesto ovog char
	// ali sustina je svakako ista
	
	// znaci u tvojoj klasi ti isto treba jedan currentIndex da bi se znalo koji kljuc treba da showPublicKey/PrivateKey
	//prikazuje
	
	// razlika sto ovde dohvata ring jedan po jedan, a kod nas ces ih dohatit odjednom pa iterirat u svojoj klasi kroz njih
	// i kljuceve
	
	
	
	
	/*
	public void showPublicKey(char sign) {
		PGPPublicKeyRing oldRing = currentPublicRing;
		if (sign == '+') {
			currentPublicIndex++;
			currentPublicRing = keyGenerator.getPublicRing(currentPublicIndex);
			if (currentPublicRing == null) {
				currentPublicRing = oldRing;
				currentPublicIndex--;
			}
		} else {
			currentPublicIndex--;
			currentPublicRing = keyGenerator.getPublicRing(currentPublicIndex);
			if (currentPublicRing == null) {
				currentPublicRing = oldRing;
				currentPublicIndex++;
			}
		}
		
		if (currentPublicRing != null) {
			java.util.Iterator<PGPPublicKey> iterPrivate = currentPublicRing.getPublicKeys();
			PGPPublicKey currentPublicKey = iterPrivate.next();
			publicKeyInfo[0][1].setText(String.valueOf(currentPublicKey.getKeyID()));
			publicKeyInfo[1][1].setText(currentPublicKey.getUserIDs().next());
			publicKeyInfo[2][1].setText(String.valueOf(currentPublicKey.getCreationTime()));
		}
	}
	*/
	
	public PGPPublicKeyRing findPublicRing(long keyID) {
		Iterator<PGPPublicKeyRing> pkrIterator = getPublicRings();
		PGPPublicKeyRing publicKeyRing = null;
		int found = 0;
		while (pkrIterator.hasNext() && found == 0) {
			publicKeyRing = pkrIterator.next();
			Iterator<PGPPublicKey> iterKey = publicKeyRing.getPublicKeys();
			while (iterKey.hasNext()) {
				PGPPublicKey publicKey = iterKey.next();
				if (keyID == publicKey.getKeyID()) {
					found = 1; break;
				}
			}
		}
		if (found == 1) {
			return publicKeyRing;
		}
		return null;
	}
	
	
	public void publicKeyExport(String savePath, long keyID) throws IOException {
		PGPPublicKeyRing publicKeyRing = findPublicRing(keyID);
		if(publicKeyRing != null) {
			ArmoredOutputStream aos = new ArmoredOutputStream(new FileOutputStream(new File(savePath)));
			publicKeyRing.encode(aos);
            aos.close();
		}
	}
}
