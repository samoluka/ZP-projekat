package etf.openpgp.sl180053kf180285.gui;

import java.awt.FileDialog;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;

import etf.openpgp.sl180053kf180285.projekat.Keys;
import etf.openpgp.sl180053kf180285.projekat.User;
import etf.openpgp.sl180053kf180285.projekat.UserProvider;
import etf.openpgp.sl180053kf180285.util.KeyFormatter;

public class ShowKeysGUI extends GUI {

	private int keyIndex = 0;
	private int importedKeyIndex = 0;
	private User u = UserProvider.getInstance().getCurrentUser();
	private List<PGPSecretKeyRing> secretKeyList;
	private JTextArea secretKeyInfo = new JTextArea();
	private JTextArea publicKeyInfo = new JTextArea();
	private JTextArea importedPublicKeyInfo = new JTextArea();
	private ArrayList<PGPPublicKeyRing> importedPublicKeyList;

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

		JPanel importedPublicKeyPanel = new JPanel();
		importedPublicKeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		importedPublicKeyPanel.setLayout(new BoxLayout(importedPublicKeyPanel, BoxLayout.X_AXIS));
		JLabel importedPublicKeyLabel = new JLabel("javni kljuc info \t");
		importedPublicKeyPanel.add(importedPublicKeyLabel);
		importedPublicKeyPanel.add(importedPublicKeyInfo);
		JPanel importedPublicKeyButtonPanel = new JPanel();
		importedPublicKeyButtonPanel.setLayout(new BoxLayout(importedPublicKeyButtonPanel, BoxLayout.Y_AXIS));
		JButton importedPrev = new JButton("prethodni");
		importedPrev.setEnabled(false);
		JButton importedNext = new JButton("sledeci");
		importedPublicKeyButtonPanel.add(importedPrev);
		importedPublicKeyButtonPanel.add(importedNext);
		importedPublicKeyPanel.add(importedPublicKeyButtonPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton prev = new JButton("prethodni");
		prev.setEnabled(false);
		JButton next = new JButton("sledeci");
		JButton deletePair = new JButton("brisanje para javni privatni kljuc");
		JButton importSecretKey = new JButton("Uvezi privatni kljuc");
		JButton importPublicKey = new JButton("Uvezi javni kljuc");
		buttonPanel.add(prev);
		buttonPanel.add(next);
		buttonPanel.add(importSecretKey);
		buttonPanel.add(importPublicKey);

		JPanel deletePanel = new JPanel();
		deletePanel.setLayout(new BoxLayout(deletePanel, BoxLayout.X_AXIS));
		JLabel deleteLabel = new JLabel("unesite sifru privatnog kljuca za brisanje");
		JTextField passwordField = new JTextField();
		deletePanel.add(deleteLabel);
		deletePanel.add(passwordField);

		publicKeyInfo.setEditable(false);
		secretKeyInfo.setEditable(false);
		importedPublicKeyInfo.setEditable(false);

		secretKeyList = new ArrayList<PGPSecretKeyRing>();
		new Keys().getSecretRings(u).forEachRemaining(key -> secretKeyList.add(key));

		importedPublicKeyList = new ArrayList<PGPPublicKeyRing>();
		new Keys().getImportedPublicRings(u).forEachRemaining(key -> importedPublicKeyList.add(key));

		next.setEnabled(keyIndex < secretKeyList.size() - 1);
		importedNext.setEnabled(importedKeyIndex < importedPublicKeyList.size() - 1);
		deletePair.setEnabled(secretKeyList.size() > 0);

		prev.addActionListener(e -> {
			keyIndex--;
			if (keyIndex < secretKeyList.size() - 1) {
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
			if (keyIndex == secretKeyList.size() - 1) {
				next.setEnabled(false);
			}
			updateInfo();
		});

		importedPrev.addActionListener(e -> {
			importedKeyIndex--;
			if (importedKeyIndex < importedPublicKeyList.size() - 1) {
				importedNext.setEnabled(true);
			}
			if (importedKeyIndex == 0) {
				importedPrev.setEnabled(false);
			}
			updateInfo();
		});
		importedNext.addActionListener(e -> {
			importedKeyIndex++;
			if (importedKeyIndex > 0) {
				importedPrev.setEnabled(true);
			}
			if (importedKeyIndex == importedPublicKeyList.size() - 1) {
				importedNext.setEnabled(false);
			}
			updateInfo();
		});

		publicKeyExportButton.addActionListener(e -> {
			String path = getChosenPath("Odaberite fajl za izvoz javnog kljuca");
			if (path == null)
				return;
			try {
				new Keys().publicKeyExport(path, secretKeyList.get(keyIndex).getPublicKey().getKeyID(), u);
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

		deletePair.addActionListener(e -> {
			PGPSecretKey secretKey = secretKeyList.get(keyIndex).getSecretKey();
			String message = "Doslo je do greske prilikom brisanja.";
			try {
				// String passHash = u.getPassword().substring(u.getUsername().length() + 5);
				if ((new Keys()).deleteSecretKey(secretKey.getKeyID(), passwordField.getText(), u)) {
					secretKeyList.remove(keyIndex);
					if (secretKeyList.size() > 0)
						keyIndex = keyIndex % secretKeyList.size();
					else
						keyIndex = 0;
					prev.setEnabled(keyIndex != 0);
					next.setEnabled(keyIndex != secretKeyList.size() - 1);
					message = "Obrisan je par javni privatni kljuc.";
					updateInfo();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			showDeletedView(message);
		});

		importSecretKey.addActionListener(e -> {
			String path = getChosenPath("Odaberite fajl za uvoz privatnog kljuca");
			if (path == null)
				return;
			try {
				new Keys().secretKeyImport(path, u);
				((MainGui) getParent()).setInnerPanel(new ShowKeysGUI(returnPanel, parent).getPanel());
			} catch (PGPException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		importPublicKey.addActionListener(e -> {
			String path = getChosenPath("Odaberite fajl za uvoz javnog kljuca");
			if (path == null)
				return;
			try {
				new Keys().publicKeyImport(path, u);
				((MainGui) getParent()).setInnerPanel(new ShowKeysGUI(returnPanel, parent).getPanel());
			} catch (PGPException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

		updateInfo();

		panel.add(publicKeyPanel);
		panel.add(secretKeyPanel);
		panel.add(buttonPanel);
		panel.add(deletePanel);
		panel.add(deletePair);
		panel.add(importedPublicKeyPanel);

		JButton back = new JButton("nazad");
		back.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});
		panel.add(back);

		setPanel(panel);
	}

	private void showDeletedView(String message) {
		JFrame f = new JFrame();
		JOptionPane.showMessageDialog(getPanel(), message);
	}

	private void updateInfo() {
		if (importedKeyIndex >= importedPublicKeyList.size()) {
			importedPublicKeyInfo.setText("");
		} else {
			importedPublicKeyInfo.setText(KeyFormatter.getInstance()
					.publicKeyToString(importedPublicKeyList.get(importedKeyIndex).getPublicKey()));
		}
		if (keyIndex >= secretKeyList.size()) {
			publicKeyInfo.setText("");
			secretKeyInfo.setText("");
			return;
		}
		PGPPublicKey publicKey = secretKeyList.get(keyIndex).getPublicKey();
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
