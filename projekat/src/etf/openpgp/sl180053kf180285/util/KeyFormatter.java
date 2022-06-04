package etf.openpgp.sl180053kf180285.util;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

public class KeyFormatter {

	private static KeyFormatter instance = new KeyFormatter();

	private KeyFormatter() {
	};

	public static KeyFormatter getInstance() {
		return instance;
	}

	public String publicKeyToString(PGPPublicKey p) {
		return String.format("%s\n%s\n%s", p.getKeyID(), p.getUserIDs().next(), p.getCreationTime());
	}

	public String secretKeyToString(PGPSecretKey p) {
		return String.format("%s\n%s\n%s", p.getKeyID(), p.getUserIDs().next(), p.getPublicKey().getCreationTime());
	}
}
