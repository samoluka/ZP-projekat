package etf.openpgp.sl180053kf180285.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.bouncycastle.openpgp.PGPKeyPair;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import etf.openpgp.sl180053kf180285.projekat.User;
import etf.openpgp.sl180053kf180285.projekat.UserProvider;
import etf.openpgp.sl180053kf180285.util.KeyFormatter;
import etf.openpgp.sl180053kf180285.util.Pair;

public class KeyPairViewGUI extends GUI {

	private Pair<PGPKeyPair, PGPKeyPair> pair;

	public KeyPairViewGUI(Pair<PGPKeyPair, PGPKeyPair> pair, JPanel returnPanel, GUI parent) {
		setParent(parent);
		this.pair = pair;

		JPanel panel = new JPanel();

		User user = UserProvider.getInstance().getCurrentUser();

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

		setPanel(panel);
	}

}
