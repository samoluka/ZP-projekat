package projekat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;

public class ZipRadix {

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

	public static byte[] decompressData(byte[] message) throws IOException, PGPException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(message);
		Object comData = objectFactory.nextObject();
		PGPCompressedData compressedData = (PGPCompressedData) comData;

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = compressedData.getInputStream().read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		return buffer.toByteArray();

		/*
		 * JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data); Object o =
		 * objectFactory.nextObject(); PGPCompressedData cdata = (PGPCompressedData) o;
		 * return cdata.getDataStream().readAllBytes();
		 */
	}

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

	public static byte[] radixDeconversion(byte[] message) throws IOException {
		
		  ByteArrayInputStream byteInputStream = new ByteArrayInputStream(message);
		  ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		  
		  int nRead; byte[] data = new byte[16384];
		  
		  while ((nRead = PGPUtil.getDecoderStream(byteInputStream).read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead); }
		  
		  return buffer.toByteArray();
		  }
		//byte[] dencodeBytes = org.bouncycastle.util.encoders.Base64.decode(message);
		//return dencodeBytes;

	}

