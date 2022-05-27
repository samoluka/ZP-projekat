package projekat;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;
import java.util.Base64.Encoder;

public class GenerateRSAKeys {

	private static GenerateRSAKeys instance = new GenerateRSAKeys();

	public static GenerateRSAKeys getInsance() {
		return instance;
	}

	// dodat keyLength da mozemo proizvoljno da setujemo duzinu kljuca RSA
	public KeyPair generate(/* String publicKeyFilename, String privateFilename, */ int keyLength) {

		try {

			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

			// Create the public and private keys
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
			Encoder b64 = Base64.getEncoder(); // umjesto onog sa sajta uzet Base64 iz bouncy castle, msm da radi onako
												// kako su ovi sa sajta htjeli

			// SecureRandom random = createFixedRandom();
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			generator.initialize(keyLength, secureRandom);

			KeyPair pair = generator.genKeyPair();
//				Key pubKey = pair.getPublic();
//				Key privKey = pair.getPrivate();

//				System.out.println("publicKey : " + new String(b64.encode(pubKey.getEncoded())));
//				System.out.println("privateKey : " + new String(b64.encode(privKey.getEncoded())));
			//
//				BufferedWriter out = new BufferedWriter(new FileWriter(publicKeyFilename));
//				out.write(new String(b64.encode(pubKey.getEncoded())));
//				out.close();
			//
//				out = new BufferedWriter(new FileWriter(privateFilename));
//				out.write(new String(b64.encode(privKey.getEncoded())));
//				out.close();
			return pair;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

}
