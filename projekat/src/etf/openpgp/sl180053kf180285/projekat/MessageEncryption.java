package etf.openpgp.sl180053kf180285.projekat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;

import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;

public class MessageEncryption {

	private static MessageEncryption instance = new MessageEncryption();

	private MessageEncryption() {
	}

	/**
	 * vraca instancu klase MessageEncryption
	 */
	public static MessageEncryption getInstance() {
		return instance;
	}

	/**
	 * sifrovanje poruke odabranim simetricnim algoritmom, uz koriscenje izabranog javnog kljuca
	 */
	public byte[] encryptMessage(PGPPublicKey encryptionKey, byte[] data, int algorithm)
			throws PGPException, IOException {
		PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(new JcePGPDataEncryptorBuilder(algorithm)
				.setWithIntegrityPacket(true).setSecureRandom(new SecureRandom()).setProvider("BC"));
		encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encryptionKey).setProvider("BC"));
		ByteArrayOutputStream encOut = new ByteArrayOutputStream();
		// create an indefinite length encrypted stream
		OutputStream cOut = encGen.open(encOut, data.length);
		// write out the literal data
		// PGPLiteralDataGenerator lData = new PGPLiteralDataGenerator();
		// OutputStream pOut = lData.open(cOut, PGPLiteralData.BINARY,
		// PGPLiteralData.CONSOLE, data.length, new Date());
		cOut.write(data);
		cOut.close();
		// finish the encryption
		cOut.close();
		return encOut.toByteArray();
	}

	/**
	 * provera da li je poruka sifrovana, radi eventualnog pozivanja funkcije decryptMessage
	 */
	public boolean isEncrypted(byte[] message) {
		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(message);
		Object enc = null;
		try {
			enc = objectFactory.nextObject();
		} catch (IOException e) {
			return false;
		}
		if (enc instanceof PGPEncryptedDataList)
			return true;
		return false;
	}
}
