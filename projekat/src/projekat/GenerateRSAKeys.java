package projekat;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Date;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPKeyRingGenerator;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.operator.PGPDigestCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPDigestCalculatorProviderBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyPair;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyEncryptorBuilder;

public class GenerateRSAKeys {

	private static GenerateRSAKeys instance = new GenerateRSAKeys();

	public static GenerateRSAKeys getInsance() {
		return instance;
	}

	// moze argumenti da se citaju i iz currentUser
	public void generate(String name, String mail, String password, int keySize) throws NoSuchProviderException,PGPException,NoSuchAlgorithmException, IOException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // postavljanje BC provajdera
		
		User currentUser = UserProvider.getInstance().getUserByUsername(UserProvider.getCurrentUser()); // dohvatanje trenutnog korisnika
		
		KeyPairGenerator generatorRSA = KeyPairGenerator.getInstance("RSA","BC"); // init generatora
		generatorRSA.initialize(keySize);
		
		KeyPair masterKeyPair = generatorRSA.generateKeyPair(); // ne znam jos za sta su dva para kljuceva ovde
		KeyPair keyPair = generatorRSA.generateKeyPair();
		
		// kreiranje pgp para kljuceva od java para kljuceva
		PGPKeyPair pgpMasterKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_SIGN, masterKeyPair, new Date());
		PGPKeyPair pgpKeyPair = new JcaPGPKeyPair(PGPPublicKey.RSA_ENCRYPT, keyPair, new Date());
		 
		// za hash(vrv za cuvanje passworda u hashu sha1 u prstenu priv kljuceva)
		PGPDigestCalculator sha1DigestCalculator = new JcaPGPDigestCalculatorProviderBuilder().build().get(HashAlgorithmTags.SHA1);
		 
		//za generisanje prstenova javnih i privatnih kljuceva
		PGPKeyRingGenerator keyRingGenerator = new PGPKeyRingGenerator(
				PGPSignature.POSITIVE_CERTIFICATION,
				pgpMasterKeyPair,
				name + "#" + mail, 
				sha1DigestCalculator,
				null,
				null,
				new JcaPGPContentSignerBuilder(PGPPublicKey.RSA_SIGN, HashAlgorithmTags.SHA1),
				new JcePBESecretKeyEncryptorBuilder(PGPEncryptedData.AES_256, sha1DigestCalculator).setProvider("BC").build(password.toCharArray())
		);
		
		keyRingGenerator.addSubKey(pgpKeyPair);
		
		PGPSecretKeyRing privateKeyRing = keyRingGenerator.generateSecretKeyRing();
		PGPPublicKeyRing publicKeyRing = keyRingGenerator.generatePublicKeyRing();
		
		
		currentUser.setSecretKeyRingCollection(PGPSecretKeyRingCollection.addSecretKeyRing(currentUser.getSecretKeyRingCollection(), privateKeyRing));
		
		currentUser.setPublicKeyRingCollection(PGPPublicKeyRingCollection.addPublicKeyRing(currentUser.getPublicKeyRingCollection(), publicKeyRing));
		
		// upis u .asc fajl
		ArmoredOutputStream aos1 = new ArmoredOutputStream(new FileOutputStream(currentUser.getSecretKeyRingDirectory()));
		(currentUser.getSecretKeyRingCollection()).encode(aos1);
        aos1.close();
        
        ArmoredOutputStream aos2 = new ArmoredOutputStream(new FileOutputStream(currentUser.getPublicKeyRingDirectory()));
		(currentUser.getPublicKeyRingCollection()).encode(aos1);
        aos2.close();
        
	}

}
