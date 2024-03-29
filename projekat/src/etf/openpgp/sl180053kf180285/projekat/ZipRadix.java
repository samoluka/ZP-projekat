package etf.openpgp.sl180053kf180285.projekat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;
import org.bouncycastle.util.io.Streams;

/**
 * 
 * Usluzna klasa sa statickim metodama koje omogucavaju kompresiju,
 * dekompresiju, konverziju u radix64, dekonverziju iz radix64, kao i proveru da
 * li je poruka komprimovana.
 *
 */

public class ZipRadix {

	/**
	 * kompresuje prosledjenu poruku u vidu niza bajtova
	 */
	public static byte[] compressMessage(byte[] message) throws IOException {
		PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(
				CompressionAlgorithmTags.ZIP);
		ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
		OutputStream dataOutput = compressedDataGenerator.open(compressedOutput);
		// update bytes in the stream
		dataOutput.write(message);
		dataOutput.close();
		message = compressedOutput.toByteArray();
		compressedOutput.close();
		return message;
	}

	/**
	 * proverava da li je poruka komprimovana, radi moguceg kasnijeg pozivanja
	 * funkcije za dekompresiju
	 */
	public static boolean checkIfCompressed(byte[] message) {
		PGPObjectFactory objectFactory = new JcaPGPObjectFactory(message);
		Object compMess = null;
		try {
			compMess = objectFactory.nextObject();
		} catch (IOException e) {
			return false;
		}
		if (compMess instanceof PGPCompressedData) {
			return true;
		}
		return false;
	}

	/**
	 * dekompresuje prosledjenu poruku u vidu niza bajtova
	 */
	public static byte[] decompressData(byte[] message) throws IOException, PGPException {
		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(message);
		Object o = objectFactory.nextObject();
		PGPCompressedData cdata = (PGPCompressedData) o;
		return Streams.readAll(cdata.getDataStream());
	}

	/**
	 * konverzija poruke u radix64 format
	 */
	public static byte[] convertToRadix64(byte[] message) throws IOException {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteOutputStream);
		armoredOutputStream.write(message);
		armoredOutputStream.close();
		message = byteOutputStream.toByteArray();
		byteOutputStream.close();
		return message;
		// return org.bouncycastle.util.encoders.Base64.encode(message);
	}

	/**
	 * dekonverzija poruke iz radix64 formata
	 */
	public static byte[] radixDeconversion(byte[] message) throws IOException {

		ByteArrayInputStream byteInputStream = new ByteArrayInputStream(message);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = PGPUtil.getDecoderStream(byteInputStream).read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		return buffer.toByteArray();
	}

}
