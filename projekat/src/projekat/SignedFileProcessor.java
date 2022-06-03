package projekat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;

import org.bouncycastle.bcpg.BCPGOutputStream;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

public class SignedFileProcessor {
	/*
	 * verify the passed in file as being correctly signed.
	 */
	public boolean verifyFile(byte[] data, User u) throws Exception {

		JcaPGPObjectFactory pgpFact = new JcaPGPObjectFactory(data);

		// PGPCompressedData c1 = (PGPCompressedData) pgpFact.nextObject();

		// pgpFact = new JcaPGPObjectFactory(c1.getDataStream());

		PGPOnePassSignatureList p1 = (PGPOnePassSignatureList) pgpFact.nextObject();

		PGPOnePassSignature ops = p1.get(0);

		PGPLiteralData p2 = (PGPLiteralData) pgpFact.nextObject();

		PGPPublicKey pubKey = new Keys().findPublicRing(ops.getKeyID(), u).getFirst();

		ops.init(new JcaPGPContentVerifierBuilderProvider().setProvider("BC"), pubKey);

		InputStream dIn = p2.getInputStream();
		int ch;
		while ((ch = dIn.read()) >= 0) {
			ops.update((byte) ch);
		}

		PGPSignatureList p3 = (PGPSignatureList) pgpFact.nextObject();

		if (ops.verify(p3.get(0))) {
			System.out.println(
					"verifikovan potpis :" + new String(pubKey.getRawUserIDs().next(), StandardCharsets.UTF_8));
			return true;
		} else {
			System.out.println("signature verification failed.");
			return false;
		}
	}

	public byte[] signFile(byte[] data, String password, PGPSecretKey secretKey) throws IOException, PGPException {
		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		PGPSecretKey pgpSec = secretKey;
		PGPPrivateKey pgpPrivKey = pgpSec.extractPrivateKey(
				new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build(password.toCharArray()));
		PGPSignatureGenerator sGen = new PGPSignatureGenerator(
				new JcaPGPContentSignerBuilder(pgpSec.getPublicKey().getAlgorithm(), PGPUtil.SHA1).setProvider("BC"));

		sGen.init(PGPSignature.BINARY_DOCUMENT, pgpPrivKey);

		Iterator it = pgpSec.getPublicKey().getUserIDs();
		if (it.hasNext()) {
			PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();

			spGen.addSignerUserID(false, (String) it.next());
			sGen.setHashedSubpackets(spGen.generate());
		}

		// PGPCompressedDataGenerator cGen = new
		// PGPCompressedDataGenerator(PGPCompressedData.ZLIB);

		BCPGOutputStream bOut = new BCPGOutputStream(bStream);

		sGen.generateOnePassVersion(false).encode(bOut);

		PGPLiteralDataGenerator lGen = new PGPLiteralDataGenerator();
		OutputStream outputStream = lGen.open(bOut, PGPLiteralData.BINARY, "_CONSOLE", data.length, new Date());

		for (byte ch : data) {
			outputStream.write(ch);
			sGen.update((byte) ch);
		}

		lGen.close();

		sGen.generate().encode(bOut);

		// cGen.close();

		data = bStream.toByteArray();

		bStream.close();

		bOut.close();

		outputStream.close();

		return data;
	}
}