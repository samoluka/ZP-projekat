package etf.openpgp.sl180053kf180285.projekat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Racuna hes korisnikove sifre za logovanje, koristi se pri cuvanju korisnika u
 * fajl users.txt.
 * 
 */
public class HashText {

	/**
	 * racuna hes prosledjenog teksta koristeci SHA1 algoritam
	 */
	public static String getSHA(String text) {
		String generatedText = null;

		try {
			// Create MessageDigest instance for MD5
			MessageDigest md = MessageDigest.getInstance("SHA1");

			// Add password bytes to digest
			md.update(text.getBytes());

			// Get the hash's bytes
			byte[] bytes = md.digest();

			// This bytes[] has bytes in decimal format. Convert it to hexadecimal format
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}

			// Get complete hashed password in hex format
			generatedText = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.toString());
		}

		return generatedText;

	}
}
