package etf.openpgp.sl180053kf180285.util;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

/**
 * 
 * Singleton klasa, sluzi za ispis kljuceva, ispisuje se ID kljuca, ID korisnika
 * i vreme kreiranja kljuca.
 *
 */
public class KeyFormatter {

	private static KeyFormatter instance = new KeyFormatter();

	private KeyFormatter() {
	};

	/**
	 * dohvata instancu KeyFormatter
	 */
	public static KeyFormatter getInstance() {
		return instance;
	}

	/**
	 * ispis javnog kljuca
	 */
	public String publicKeyToString(PGPPublicKey p) {
		return String.format("%s\n%s\n%s", p.getKeyID(), p.getUserIDs().next(), p.getCreationTime());
	}

	/**
	 * ispis privatnog kljuca
	 */
	public String secretKeyToString(PGPSecretKey p) {
		return String.format("%s\n%s\n%s", p.getKeyID(), p.getUserIDs().next(), p.getPublicKey().getCreationTime());
	}
}
