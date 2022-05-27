package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyPair;

import projekat.GenerateRSAKeys;
import util.Pair;

public class GenereteKeyPairGUI extends GUI {

	String pub = null;
	String priv = null;

	public GenereteKeyPairGUI(GUI parent) {
		setParent(parent);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));

		JLabel usernameLabel = new JLabel("korisnicko ime: ");
		JLabel emailLabel = new JLabel("e-mail:");
		JLabel passLabel = new JLabel("sifra:");
		JLabel lengthLabel = new JLabel("duzina kljuca:");

		JTextField username = new JTextField(30);
		JTextField email = new JTextField(30);
		JTextField pass = new JTextField(30);
		Integer[] optionsToChoose = { 1024, 2048, 4096 };
		JComboBox<Integer> lengthDropDown = new JComboBox<>(optionsToChoose);

		JPanel usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, 0));
		usernamePanel.add(usernameLabel);
		usernamePanel.add(username);

		JPanel emailPanel = new JPanel();
		emailPanel.setLayout(new BoxLayout(emailPanel, 0));
		emailPanel.add(emailLabel);
		emailPanel.add(email);

		JPanel passPanel = new JPanel();
		passPanel.setLayout(new BoxLayout(passPanel, 0));
		passPanel.add(passLabel);
		passPanel.add(pass);

		JPanel lengthPanel = new JPanel();
		lengthPanel.setLayout(new BoxLayout(lengthPanel, 0));
		lengthPanel.add(lengthLabel);
		lengthPanel.add(lengthDropDown);

		panel.add(usernamePanel);
		panel.add(emailPanel);
		panel.add(passPanel);
		panel.add(lengthPanel);

		JButton generateButton = new JButton("Generisi kljuc");

		generateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();

				GenerateRSAKeys generator = GenerateRSAKeys.getInsance();
				Pair<PGPKeyPair, PGPKeyPair> pair;
				try {
					pair = generator.generate((Integer) lengthDropDown.getSelectedItem());
					((MainGui) getParent()).setInnerPanel((new KeyPairViewGUI(pair, panel, getParent())).getPanel());
				} catch (NoSuchProviderException | NoSuchAlgorithmException | PGPException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
//				Encoder b64 = Base64.getEncoder();
//				String nPub = new String(b64.encode(pair.getPublic().getEncoded()));
//				String nPriv = new String(b64.encode(pair.getPrivate().getEncoded()));
//				System.out.println(String.format("public key: %s", nPub));
//				System.out.println(String.format("private key: %s", nPriv));
//				if (nPub.equals(pub)) {
//					System.out.println("Isti javni kljuc");
//				}
//				if (nPriv.equals(priv)) {
//					System.out.println("Isti tajni kljuc");
//				}
//				priv = nPriv;
//				pub = nPub;

			}
		});

		panel.add(generateButton);
		setPanel(panel);

	}

}
