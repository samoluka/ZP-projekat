package gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import projekat.Keys;
import projekat.User;
import projekat.UserProvider;
import util.KeyFormatter;

public class ShowKeysGUI extends GUI {

	private int keyIndex = 0;
	private User u = UserProvider.getInstance().getCurrentUser();
	private List<PGPPublicKeyRing> publicKeyList;
	private List<PGPSecretKeyRing> secretKeyList;
	private JLabel secretKeyInfo = new JLabel();
	private JLabel publicKeyInfo = new JLabel();

	public ShowKeysGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel publicKeyPanel = new JPanel();
		publicKeyPanel.setLayout(new BoxLayout(publicKeyPanel, BoxLayout.X_AXIS));
		JLabel publicKeyLabel = new JLabel("javni kljuc info");

		publicKeyPanel.add(publicKeyLabel);
		publicKeyPanel.add(publicKeyInfo);

		JPanel secretKeyPanel = new JPanel();
		secretKeyPanel.setLayout(new BoxLayout(secretKeyPanel, BoxLayout.X_AXIS));
		JLabel secretKeyLabel = new JLabel("privatni kljuc ingo");
		secretKeyPanel.add(secretKeyLabel);
		secretKeyPanel.add(secretKeyInfo);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton prev = new JButton("prethodni");
		prev.setEnabled(false);
		JButton next = new JButton("sledeci");
		next.setEnabled(keyIndex == publicKeyList.size() - 1);
		buttonPanel.add(prev);
		buttonPanel.add(next);

		publicKeyList = new ArrayList<PGPPublicKeyRing>();
		new Keys().getPublicRings(u).forEachRemaining(key -> publicKeyList.add(key));

		secretKeyList = new ArrayList<PGPSecretKeyRing>();
		new Keys().getSecretRings(u).forEachRemaining(key -> secretKeyList.add(key));

		prev.addActionListener(e -> {
			keyIndex--;
			if (keyIndex < publicKeyList.size() - 1) {
				next.setEnabled(true);
			}
			if (keyIndex == 0) {
				prev.setEnabled(false);
			}
			updateInfo();
		});
		next.addActionListener(e -> {
			keyIndex++;
			if (keyIndex > 0) {
				prev.setEnabled(true);
			}
			if (keyIndex == publicKeyList.size() - 1) {
				next.setEnabled(false);
			}
			updateInfo();
		});

		updateInfo();

		panel.add(publicKeyPanel);
		panel.add(secretKeyPanel);
		panel.add(buttonPanel);

		JButton back = new JButton("nazad");
		back.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});
		panel.add(back);

		setPanel(panel);
	}

	private void updateInfo() {
		if (keyIndex >= publicKeyList.size()) {
			return;
		}
		PGPPublicKey publicKey = publicKeyList.get(keyIndex).getPublicKey();
		PGPSecretKey secretKey = secretKeyList.get(keyIndex).getSecretKey();

		String pub = KeyFormatter.getInstance().publicKeyToString(publicKey);
		String secret = KeyFormatter.getInstance().secretKeyToString(secretKey);

		publicKeyInfo.setText(pub);
		secretKeyInfo.setText(secret);
	}

}
