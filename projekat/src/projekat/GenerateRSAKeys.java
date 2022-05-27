package projekat;

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

	// test
	public static void main(String[] args) {

//		String publicKeyFilename = null;
//		String privateKeyFilename = null;
//
//		GenerateRSAKeys generateRSAKeys = new GenerateRSAKeys();
//
//		if (args.length < 2) {
//			System.err.println("Usage: java " + generateRSAKeys.getClass().getName()
//					+ " Public_Key_Filename Private_Key_Filename");
//			System.exit(1);
//		}
//		publicKeyFilename = args[0].trim();
//		privateKeyFilename = args[1].trim();
//		String pub = null;
//		String priv = null;
//		Encoder b64 = Base64.getEncoder();
//		for (int i = 0; i < 10; i++) {
//			KeyPair pair = generateRSAKeys.generate(publicKeyFilename, privateKeyFilename, 1024);
//			String nPub = new String(b64.encode(pair.getPublic().getEncoded()));
//			String nPriv = new String(b64.encode(pair.getPrivate().getEncoded()));
//			if (nPub.equals(pub)) {
//				System.out.println("Isti javni kljuc u iteraciji " + i);
//			}
//			if (nPriv.equals(priv)) {
//				System.out.println("Isti tajni kljuc u iteraciji " + i);
//			}
//			priv = nPriv;
//			pub = nPub;
//		}
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
//			Key pubKey = pair.getPublic();
//			Key privKey = pair.getPrivate();

//			System.out.println("publicKey : " + new String(b64.encode(pubKey.getEncoded())));
//			System.out.println("privateKey : " + new String(b64.encode(privKey.getEncoded())));
//
//			BufferedWriter out = new BufferedWriter(new FileWriter(publicKeyFilename));
//			out.write(new String(b64.encode(pubKey.getEncoded())));
//			out.close();
//
//			out = new BufferedWriter(new FileWriter(privateFilename));
//			out.write(new String(b64.encode(privKey.getEncoded())));
//			out.close();
			return pair;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static SecureRandom createFixedRandom() {
		return new FixedRand();
	}

	private static class FixedRand extends SecureRandom {

		MessageDigest sha;
		byte[] state;

		FixedRand() {
			try {
				this.sha = MessageDigest.getInstance("SHA-1");
				this.state = sha.digest();
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("can't find SHA-1!");
			}
		}

		// za sad ne koristi
		@Override
		public void nextBytes(byte[] bytes) {

			int off = 0;

			sha.update(state);

			while (off < bytes.length) {
				state = sha.digest();

				if (bytes.length - off > state.length) {
					System.arraycopy(state, 0, bytes, off, state.length);
				} else {
					System.arraycopy(state, 0, bytes, off, bytes.length - off);
				}

				off += state.length;

				sha.update(state);
			}
		}
	}

	private GenerateRSAKeys() {
	}

	public static GenerateRSAKeys getInsance() {
		return instance;
	}

}