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
import projekat.User;
import projekat.UserProvider;
import util.Pair;

public class GenereteKeyPairGUI extends GUI {

	String pub = null;
	String priv = null;

	public GenereteKeyPairGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));

		JLabel emailLabel = new JLabel("e-mail:");
		JLabel passLabel = new JLabel("sifra:");
		JLabel lengthLabel = new JLabel("duzina kljuca:");

		JTextField email = new JTextField(30);
		JTextField pass = new JTextField(30);
		Integer[] optionsToChoose = { 1024, 2048, 4096 };
		JComboBox<Integer> lengthDropDown = new JComboBox<>(optionsToChoose);

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

		panel.add(emailPanel);
		panel.add(passPanel);
		panel.add(lengthPanel);

		JButton generateButton = new JButton("Generisi kljuc");

		generateButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();

				GenerateRSAKeys generator = GenerateRSAKeys.getInsance();
				User user = UserProvider.getInstance().getCurrentUser();
				Pair<PGPKeyPair, PGPKeyPair> pair;
				try {
					pair = generator.generate((Integer) lengthDropDown.getSelectedItem());
					generator.addKeyPairToKeyRing(user, email.getText(), pass.getText(), pair.getFirst(),
							pair.getSecond());
					generator.saveKeyRingToFile(user);
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

		JButton back = new JButton("nazad");
		back.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});
		panel.add(back);

		setPanel(panel);

	}

}
