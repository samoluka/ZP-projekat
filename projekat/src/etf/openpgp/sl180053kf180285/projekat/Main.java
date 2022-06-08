package etf.openpgp.sl180053kf180285.projekat;

import java.security.Security;

import javax.swing.JFrame;

import etf.openpgp.sl180053kf180285.gui.MainGui;

/**
 * 
 * 
 * Sadrzi main metodu kojom se aplikacija pokrece.
 */

public class Main {

	/**
	 * pokretanje aplikacije
	 */
	public static void main(String[] args) {

		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); // postavljanje BC provajdera

		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1000, 800);

		MainGui mainGui = new MainGui(frame);

		frame.add(mainGui.getPanel());
		frame.setVisible(true);

		System.out.println("AJMO DONCIC");
	}
}
