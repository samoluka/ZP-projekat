package projekat;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;

public class Keys {

	public void generateKeyPair(String name, String mail, int keySize) throws NoSuchAlgorithmException, PGPException { // za
																											// generisanje
																											// parova
																											// kljuceva,
																											// pozivace
																											// se na
																											// klik
																											// dugmeta
																											// "Generisi
																											// novi par
																											// kljuceva"
		KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
		generator.initialize(keySize);
		KeyPair keyPair = generator.generateKeyPair();
		PGPKeyPair pgpkeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, keyPair, new Date()); // konverzija u pgp par kljuceva
	}

}
