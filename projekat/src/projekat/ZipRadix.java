package projekat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bouncycastle.bcpg.ArmoredOutputStream;
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
	
	/*
	public byte[] decompressData(byte[] data) throws IOException, PGPException {
		JcaPGPObjectFactory objectFactory = new JcaPGPObjectFactory(data);
        Object o = objectFactory.nextObject();
        PGPCompressedData cdata = (PGPCompressedData) o;
        return cdata.getDataStream().readAllBytes();
    }*/
	
    public static byte[] convertToRadix64(byte[] message) throws IOException {
    	ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ArmoredOutputStream armoredOutputStream = new ArmoredOutputStream(byteOutputStream);
        armoredOutputStream.write(message);
        armoredOutputStream.close();
        message = byteOutputStream.toByteArray();        
        byteOutputStream.close();
        return message;
    }
	
}
