package projekat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.jcajce.JcaPGPObjectFactory;

public class ZipRadix {

	public static byte[] compressMessage(byte[] message) throws IOException{
		PGPCompressedDataGenerator compressedDataGenerator   = new PGPCompressedDataGenerator(CompressionAlgorithmTags.ZIP);
		ByteArrayOutputStream compressedOutput = new ByteArrayOutputStream();
		OutputStream dataOutput = compressedDataGenerator.open(compressedOutput);
        // update bytes in the stream
		dataOutput.write(message);
		dataOutput.close();    	
    	message = compressedOutput.toByteArray();
    	compressedOutput.close();
        return message;
	}
	
	public static byte[] decompressMessage(byte[] message) throws IOException, PGPException{
		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(message);
        return ((PGPCompressedData) objectFactory.nextObject()).getDataStream().readAllBytes();
	}
	
}
