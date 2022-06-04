package projekat;

import java.io.IOException;
import java.io.InputStream;

import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.util.io.Streams;

public class MessageDecryption {

	private static MessageDecryption instance = new MessageDecryption();

	public static MessageDecryption getInstance() {
		return instance;
	}

	private MessageDecryption() {

	}

	public byte[] decryptMessage(PGPPrivateKey privateKey, byte[] pgpEncryptedData) throws PGPException, IOException {
		PGPObjectFactory pgpFact = new JcaPGPObjectFactory(pgpEncryptedData);
		PGPEncryptedDataList encList = (PGPEncryptedDataList) pgpFact.nextObject();
		// find the matching public key encrypted data packet.
		PGPPublicKeyEncryptedData encData = null;
		for (PGPEncryptedData pgpEnc : encList) {
			PGPPublicKeyEncryptedData pkEnc = (PGPPublicKeyEncryptedData) pgpEnc;
			if (pkEnc.getKeyID() == privateKey.getKeyID()) {
				encData = pkEnc;
				break;
			}
		}
		if (encData == null) {
			throw new IllegalStateException("matching encrypted data not found");
		}
		// build decryptor factory
		PublicKeyDataDecryptorFactory dataDecryptorFactory = new JcePublicKeyDataDecryptorFactoryBuilder()
				.setProvider("BC").build(privateKey);
		InputStream clear = encData.getDataStream(dataDecryptorFactory);
		byte[] literalData = Streams.readAll(clear);
		clear.close();
		// check data decrypts okay
		if (encData.verify()) {
			// parse out literal data
//			PGPObjectFactory litFact = new JcaPGPObjectFactory(literalData);
//			PGPLiteralData litData = (PGPLiteralData) litFact.nextObject();
//			byte[] data = Streams.readAll(litData.getInputStream());
			return literalData;
		}
		throw new IllegalStateException("modification check failed");
	}
}
