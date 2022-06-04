package etf.openpgp.sl180053kf180285.projekat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;

public class MessageDecryption {

	private static MessageDecryption instance = new MessageDecryption();

	public static MessageDecryption getInstance() {
		return instance;
	}

	private MessageDecryption() {

	}

	public byte[] decryptMessage(User u, String password, byte[] pgpEncryptedData) throws PGPException, IOException {
		PGPObjectFactory pgpFact = new JcaPGPObjectFactory(pgpEncryptedData);
		PGPEncryptedDataList encList = (PGPEncryptedDataList) pgpFact.nextObject();
		// find the matching public key encrypted data packet.
		PGPPublicKeyEncryptedData encData = (PGPPublicKeyEncryptedData) encList.getEncryptedDataObjects().next();
		PGPSecretKeyRing keyRing = new Keys().findPrivateRing(encData.getKeyID(), u);
		Iterator<PGPSecretKey> iter = keyRing.getSecretKeys();
		iter.next();
		PGPPrivateKey privateKey = iter.next().extractPrivateKey(
				new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));

		// build decryptor factory
		PublicKeyDataDecryptorFactory dataDecryptorFactory = new JcePublicKeyDataDecryptorFactoryBuilder()
				.setProvider("BC").build(privateKey);

		InputStream clear = encData.getDataStream(dataDecryptorFactory);
		byte[] literalData = Streams.readAll(clear);
		clear.close();
		// check data decrypts okay
		if (encData.verify()) {
			return literalData;
		}
		throw new IllegalStateException("modification check failed");
	}
}
