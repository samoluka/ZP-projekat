package gui;

import java.awt.FileDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

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
	private JTextArea secretKeyInfo = new JTextArea();
	private JTextArea publicKeyInfo = new JTextArea();

	public ShowKeysGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel publicKeyPanel = new JPanel();
		publicKeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		publicKeyPanel.setLayout(new BoxLayout(publicKeyPanel, BoxLayout.X_AXIS));
		JLabel publicKeyLabel = new JLabel("javni kljuc info \t");
		JButton publicKeyExportButton = new JButton("izvezi kljuc");
		publicKeyPanel.add(publicKeyLabel);
		publicKeyPanel.add(publicKeyInfo);
		publicKeyPanel.add(publicKeyExportButton);

		JPanel secretKeyPanel = new JPanel();
		secretKeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		secretKeyPanel.setLayout(new BoxLayout(secretKeyPanel, BoxLayout.X_AXIS));
		JLabel secretKeyLabel = new JLabel("privatni kljuc info \t");
		JButton secretKeyExportButton = new JButton("izvezi kljuc");
		secretKeyPanel.add(secretKeyLabel);
		secretKeyPanel.add(secretKeyInfo);
		secretKeyPanel.add(secretKeyExportButton);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton prev = new JButton("prethodni");
		prev.setEnabled(false);
		JButton next = new JButton("sledeci");
		buttonPanel.add(prev);
		buttonPanel.add(next);

		publicKeyInfo.setEditable(false);
		secretKeyInfo.setEditable(false);

		publicKeyList = new ArrayList<PGPPublicKeyRing>();
		new Keys().getPublicRings(u).forEachRemaining(key -> publicKeyList.add(key));

		secretKeyList = new ArrayList<PGPSecretKeyRing>();
		new Keys().getSecretRings(u).forEachRemaining(key -> secretKeyList.add(key));

		next.setEnabled(keyIndex < publicKeyList.size() - 1);

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

		publicKeyExportButton.addActionListener(e -> {
			String path = getChosenPath("Odaberite fajl za izvoz javnog kljuca");
			if (path == null)
				return;
			try {
				new Keys().publicKeyExport(path, publicKeyList.get(keyIndex).getPublicKey().getKeyID(), u);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		secretKeyExportButton.addActionListener(e -> {
			String path = getChosenPath("Odaberite fajl za izvoz privatnog kljuca");
			if (path == null)
				return;
			try {
				new Keys().privateKeyExport(path, secretKeyList.get(keyIndex).getSecretKey().getKeyID(), u);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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

	private String getChosenPath(String message) {
		JFrame parentFrame = new JFrame();

		FileDialog fileChooser = new FileDialog(parentFrame);
		fileChooser.setTitle(message);
		fileChooser.setFile("*.asc");
		fileChooser.setVisible(true);
		String filename = fileChooser.getDirectory() + fileChooser.getFile();

		return filename;
	}

}
