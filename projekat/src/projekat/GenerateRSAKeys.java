package projekat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

import util.Pair;

public class GenerateRSAKeys {

	private static GenerateRSAKeys instance = new GenerateRSAKeys();

	public static GenerateRSAKeys getInsance() {
		return instance;
	}

	// moze argumenti da se citaju i iz currentUser
	public Pair<PGPKeyPair, PGPKeyPair> generate(int keySize)
			throws NoSuchProviderException, PGPException, NoSuchAlgorithmException, IOException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // postavljanje BC provajdera

//		User currentUser = UserProvider.getInstance().getUserByUsername(UserProvider.getCurrentUser()); // dohvatanje trenutnog korisnika

		KeyPairGenerator generatorRSA = KeyPairGenerator.getInstance("RSA", "BC"); // init generatora
		generatorRSA.initialize(keySize);

		KeyPair masterKeyPair = generatorRSA.generateKeyPair(); // ne znam jos za sta su dva para kljuceva ovde
		KeyPair keyPair = generatorRSA.generateKeyPair();

		// kreiranje pgp para kljuceva od java para kljuceva
		PGPKeyPair pgpMasterKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, masterKeyPair, new Date());
		PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());

		return new Pair<>(pgpMasterKeyPair, pgpKeyPair);

	}

	public void addKeyPairToKeyRing(User user, PGPKeyPair pgpMasterKeyPair, PGPKeyPair pgpKeyPair) throws PGPException {
		// za hash(vrv za cuvanje passworda u hashu sha1 u prstenu priv kljuceva)
		PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder().build()
				.get(HashAlgorithmTags.SHA1);

		// za generisanje prstenova javnih i privatnih kljuceva
		// ovde treba mozda popraviti ovo da se negde cuva hash sifre pa da nema tih
		// problema za sad je ovako

		// String passHash = user.getPassword().substring(user.getUsername().length() +
		// 5);

		PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(PGPSignature.POSITIVE_CERTIFICATION,
				pgpMasterKeyPair, user.getUsername() + "#" + user.getEmail(), sha1DigestCalculator, null, null,
				new JcaPGPContentSignerBuilder(PGPPublicKey.RSA_SIGN, HashAlgorithmTags.SHA1),
				new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1DigestCalculator).setProvider("BC")
						.build(user.getPassword().toCharArray()));

		keyRingGenerator.addSubKey(pgpKeyPair);

		PGPSecretKeyRing privateKeyRing = keyRingGenerator.generateSecretKeyRing();
		PGPPublicKeyRing publicKeyRing = keyRingGenerator.generatePublicKeyRing();

		user.setSecretKeyRingCollection(
				PGPSecretKeyRingCollection.addSecretKeyRing(user.getSecretKeyRingCollection(), privateKeyRing));

	}

	public void saveKeyRingToFile(User user) throws IOException {
		// upis u .asc fajl
		ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(user.getSecretKeyRingDirectory()));
		(user.getSecretKeyRingCollection()).encode(aos1);
		aos1.close();
	}

}
