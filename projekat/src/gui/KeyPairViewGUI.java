package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import projekat.GenerateRSAKeys;
import projekat.User;
import projekat.UserProvider;
import util.KeyFormatter;
import util.Pair;

public class KeyPairViewGUI extends GUI {

	private Pair<PGPKeyPair, PGPKeyPair> pair;

	public KeyPairViewGUI(Pair<PGPKeyPair, PGPKeyPair> pair, JPanel returnPanel, GUI parent) {
		setParent(parent);
		this.pair = pair;

		JPanel panel = new JPanel();
		try {

			Encoder b64 = Base64.getEncoder();

			User user = UserProvider.getInstance().getCurrentUser();

			GenerateRSAKeys.getInsance().addKeyPairToKeyRing(user, pair.getFirst(), pair.getSecond());
			GenerateRSAKeys.getInsance().saveKeyRingToFile(user);

			// ovo verovatno nije dobro dohvatanje nem pojma
			PGPSecretKeyRing ring = user.getSecretKeyRingCollection().getKeyRings().next();
			PGPSecretKey privateKey = ring.getSecretKey();
			String priv = KeyFormatter.getInstance().secretKeyToString(privateKey);
			PGPPublicKey publicKey = ring.getPublicKey();
			String pub = KeyFormatter.getInstance().publicKeyToString(publicKey);
			JButton pubButton = new JButton("pogledaj javni kljuc");
			JButton privButton = new JButton("Pogledaj privatni kljuc");
			JButton back = new JButton("nazad");

			JTextArea output = new JTextArea();
			output.setLineWrap(true);
			output.setEditable(false);
			output.setVisible(false);

			pubButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					output.setText(String.format("Public key info:\n%s", pub));
					output.setVisible(true);
				}
			});

			privButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					output.setText(String.format("Private key info:\n%s", priv));
					output.setVisible(true);
				}
			});

			back.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					((MainGui) getParent()).setInnerPanel(returnPanel);
				}
			});

			panel.setLayout(new BoxLayout(panel, 1));
			panel.add(pubButton);
			panel.add(privButton);
			panel.add(back);
			panel.add(output);
		} catch (PGPException | IOException e1) {
			// TODO Auto-generated catch block
			panel.add(new JLabel("Doslo je do greske prilikom generisanja kljuceva"));
			e1.printStackTrace();
		}

		setPanel(panel);
	}

}
