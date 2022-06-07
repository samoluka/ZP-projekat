package etf.openpgp.sl180053kf180285.projekat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import etf.openpgp.sl180053kf180285.util.Pair;

public class GenerateRSAKeys {

	private static GenerateRSAKeys instance = new GenerateRSAKeys();

	/**
	 * vraca instancu klase GenerateRSAKeys
	 */
	public static GenerateRSAKeys getInsance() {
		return instance;
	}

	/**
	 * kreira kljuceve duzine parametra keySize
	 */
	public Pair<PGPKeyPair, PGPKeyPair> generate(int keySize)
			throws NoSuchProviderException, PGPException, NoSuchAlgorithmException, IOException {

//		User currentUser = UserProvider.getInstance().getUserByUsername(UserProvider.getCurrentUser()); // dohvatanje trenutnog korisnika

		KeyPairGenerator generatorRSA = KeyPairGenerator.getInstance("RSA", "BC");
		generatorRSA.initialize(keySize);

		KeyPair masterKeyPair = generatorRSA.generateKeyPair();
		KeyPair keyPair = generatorRSA.generateKeyPair();

		PGPKeyPair pgpMasterKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, masterKeyPair, new Date());
		PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());

		return new Pair<>(pgpMasterKeyPair, pgpKeyPair);

	}

	/**
	 * dodavanje para kljuceva u prsten i cuvanje u kolekciji trenutno ulogovanog korisnika
	 */
	public void addKeyPairToKeyRing(User u, String mail, String password, PGPKeyPair pgpMasterKeyPair,
			PGPKeyPair pgpKeyPair) throws PGPException {
		PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder().build()
				.get(HashAlgorithmTags.SHA1);

		PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION,
				pgpMasterKeyPair, u.getUsername() + "#" + mail, sha1DigestCalculator, null, null,
				new JcaPGPContentSignerBuilder(PGPPublicKey.RSA_SIGN, HashAlgorithmTags.SHA1),
				new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1DigestCalculator).setProvider("BC")
						.build(password.toCharArray()));

		keyRingGenerator.addSubKey(pgpKeyPair);

		PGPSecretKeyRing privateKeyRing = keyRingGenerator.generateSecretKeyRing();

		u.setSecretKeyRingCollection(
				PGPSecretKeyRingCollection.addSecretKeyRing(u.getSecretKeyRingCollection(), privateKeyRing));

	}

	/**
	 * cuva prstenove korisnika u .asc formatu
	 */
	public void saveKeyRingToFile(User user) throws IOException {
		ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(user.getSecretKeyRingDirectory()));
		(user.getSecretKeyRingCollection()).encode(aos1);
		aos1.close();
	}

}
