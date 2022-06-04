package gui;

import java.awt.Component;
import java.awt.FileDialog;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;

import projekat.Keys;
import projekat.MessageEncryption;
import projekat.SignedFileProcessor;
import projekat.User;
import projekat.UserProvider;
import projekat.ZipRadix;
import util.KeyFormatter;

public class CreateMessageGUI extends GUI {

	private ArrayList<PGPPublicKey> publicEncryptionKeyList;
	private ArrayList<PGPPublicKey> publicSigningKeyList;
	private ArrayList<PGPSecretKey> secretKeyList;
	private int encryptionKeyIndex = 0;
	private JTextArea encryptionKeyInfo;
	private JTextArea signingKeyInfo;
	private int signingKeyIndex = 0;

	public CreateMessageGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();

		User u = UserProvider.getInstance().getCurrentUser();

		JButton inputButton = new JButton("Putanja ulaznog fajla: ");
		JLabel inputPath = new JLabel();
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.add(inputButton);
		inputPanel.add(inputPath);

		JButton output = new JButton("Putanja izlaznog fajla: ");
		JLabel outputPath = new JLabel();
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
		outputPanel.add(output);
		outputPanel.add(outputPath);

		JPanel keyPanel = new JPanel();
		keyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		keyPanel.setLayout(new BoxLayout(keyPanel, BoxLayout.X_AXIS));

		encryptionKeyInfo = new JTextArea();
		JLabel encryptionKeyLabel = new JLabel("kljuc za enkripciju\t");
		encryptionKeyInfo.setEditable(false);
		JButton encryptionPrev = new JButton("prethodni");
		encryptionPrev.setEnabled(false);
		JButton encryptionNext = new JButton("sledeci");
		JPanel encryptionButtonPanel = new JPanel();
		encryptionButtonPanel.setLayout(new BoxLayout(encryptionButtonPanel, BoxLayout.X_AXIS));
		encryptionButtonPanel.add(encryptionPrev);
		encryptionButtonPanel.add(encryptionNext);

		JLabel encryptionLabel = new JLabel("enkriptuj fajl");
		JCheckBox encryptionCheckBox = new JCheckBox();
		JPanel encryptionCheckBoxPanel = new JPanel();
		encryptionCheckBoxPanel.setLayout(new BoxLayout(encryptionCheckBoxPanel, BoxLayout.X_AXIS));
		encryptionCheckBoxPanel.add(encryptionLabel);
		encryptionCheckBoxPanel.add(encryptionCheckBox);

		JPanel encryptionPanel = new JPanel();
		encryptionPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		encryptionPanel.setLayout(new BoxLayout(encryptionPanel, BoxLayout.Y_AXIS));
		encryptionPanel.add(encryptionKeyLabel);
		encryptionPanel.add(encryptionKeyInfo);
		encryptionPanel.add(encryptionButtonPanel);
		encryptionPanel.add(encryptionCheckBoxPanel);

		encryptionCheckBox.setSelected(true);
		encryptionCheckBox.addActionListener(e -> {
			setEnabled(encryptionPanel, encryptionCheckBox.isSelected());
			encryptionCheckBox.setEnabled(true);
			encryptionLabel.setEnabled(true);
			if (encryptionCheckBox.isSelected())
				setButtons(encryptionNext, encryptionPrev, publicEncryptionKeyList.size(), encryptionKeyIndex);
		});

		signingKeyInfo = new JTextArea();
		JLabel signingKeyLabel = new JLabel("kljuc za potpisivanje\t");
		signingKeyInfo.setEditable(false);
		JButton signingPrev = new JButton("prethodni");
		signingPrev.setEnabled(false);
		JButton signingNext = new JButton("sledeci");
		JPanel signingButtonPanel = new JPanel();
		signingButtonPanel.setLayout(new BoxLayout(signingButtonPanel, BoxLayout.X_AXIS));
		signingButtonPanel.add(signingPrev);
		signingButtonPanel.add(signingNext);
		JLabel signingPasswordLabel = new JLabel("sifra kljuca:\t");
		JTextField signingPassword = new JTextField();
		JPanel signingPasswordPanel = new JPanel();
		signingPasswordPanel.setLayout(new BoxLayout(signingPasswordPanel, BoxLayout.X_AXIS));
		signingPasswordPanel.add(signingPasswordLabel);
		signingPasswordPanel.add(signingPassword);

		JLabel signingLabel = new JLabel("Potpisi fajl");
		JCheckBox signingCheckBox = new JCheckBox();
		JPanel signingCheckBoxPanel = new JPanel();
		signingCheckBoxPanel.setLayout(new BoxLayout(signingCheckBoxPanel, BoxLayout.X_AXIS));
		signingCheckBoxPanel.add(signingLabel);
		signingCheckBoxPanel.add(signingCheckBox);

		JPanel signingPanel = new JPanel();
		signingPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		signingPanel.setLayout(new BoxLayout(signingPanel, BoxLayout.Y_AXIS));
		signingPanel.add(signingKeyLabel);
		signingPanel.add(signingKeyInfo);
		signingPanel.add(signingButtonPanel);
		signingPanel.add(signingPasswordPanel);
		signingPanel.add(signingCheckBoxPanel);

		signingCheckBox.setSelected(true);
		signingCheckBox.addActionListener(e -> {
			setEnabled(signingPanel, signingCheckBox.isSelected());
			signingCheckBox.setEnabled(true);
			signingLabel.setEnabled(true);
			if (signingCheckBox.isSelected())
				setButtons(signingNext, signingPrev, secretKeyList.size(), signingKeyIndex);
		});

		keyPanel.add(signingPanel);
		keyPanel.add(encryptionPanel);

		JLabel zipLabel = new JLabel("Zipuj fajl");
		JCheckBox zipCheckBox = new JCheckBox();
		JPanel zipCheckBoxPanel = new JPanel();
		zipCheckBoxPanel.setLayout(new BoxLayout(zipCheckBoxPanel, BoxLayout.X_AXIS));
		zipCheckBoxPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		zipCheckBoxPanel.add(zipLabel);
		zipCheckBoxPanel.add(zipCheckBox);

		JLabel radixLabel = new JLabel("Radixuj fajl");
		JCheckBox radixCheckBox = new JCheckBox();
		JPanel radixCheckBoxPanel = new JPanel();
		radixCheckBoxPanel.setLayout(new BoxLayout(radixCheckBoxPanel, BoxLayout.X_AXIS));
		radixCheckBoxPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		radixCheckBoxPanel.add(radixLabel);
		radixCheckBoxPanel.add(radixCheckBox);

		JPanel checkBoxPanel = new JPanel();
		checkBoxPanel.setLayout(new BoxLayout(checkBoxPanel, BoxLayout.X_AXIS));
		checkBoxPanel.add(zipCheckBoxPanel);
		checkBoxPanel.add(radixCheckBoxPanel);

		publicEncryptionKeyList = new ArrayList<PGPPublicKey>();
		publicSigningKeyList = new ArrayList<>();
		secretKeyList = new ArrayList<>();
		new Keys().getSecretRings(u).forEachRemaining(key -> {
			Iterator<PGPPublicKey> iter = key.getPublicKeys();
			Iterator<PGPSecretKey> secretIter = key.getSecretKeys();
			while (secretIter.hasNext()) {
				PGPSecretKey kSec = secretIter.next();
				if (kSec.isMasterKey()) {
					secretKeyList.add(kSec);
					break;
				}
			}
			publicSigningKeyList.add(iter.next());
			publicEncryptionKeyList.add(iter.next());
		});

		new Keys().getImportedPublicRings(u).forEachRemaining(key -> {
			Iterator<PGPPublicKey> iter = key.getPublicKeys();
			PGPPublicKey pKey = iter.next();
			System.out.println(pKey.isEncryptionKey());
			publicSigningKeyList.add(pKey);
			pKey = iter.next();
			publicEncryptionKeyList.add(pKey);
		});

		encryptionNext.setEnabled(encryptionKeyIndex < publicEncryptionKeyList.size() - 1);
		signingNext.setEnabled(signingKeyIndex < secretKeyList.size() - 1);

		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.X_AXIS));
		JLabel algorithm = new JLabel("Odaberi algoritam");
		String[] optionsToChoose = { "3DES", "AES" };
		JComboBox<String> lengthDropDown = new JComboBox<>(optionsToChoose);
		algorithmPanel.add(algorithm);
		algorithmPanel.add(lengthDropDown);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		encryptionPrev.addActionListener(e -> {
			previous(encryptionNext, encryptionPrev, publicEncryptionKeyList.size(), 'E');
		});
		encryptionNext.addActionListener(e -> {
			next(encryptionNext, encryptionPrev, publicEncryptionKeyList.size(), 'E');
		});

		signingPrev.addActionListener(e -> {
			previous(signingNext, signingPrev, secretKeyList.size(), 'S');
		});
		signingNext.addActionListener(e -> {
			next(signingNext, signingPrev, secretKeyList.size(), 'S');
		});

		JButton encrypt = new JButton("sifruj poruku");
		JButton back = new JButton("Nazad");
		encrypt.addActionListener(e -> {
			String path = outputPath.getText();
			File f = new File(inputPath.getText());
			byte[] msg = new byte[(int) f.length()];
			try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(f))) {
				dataInputStream.readFully(msg);
			} catch (IOException e3) {
				e3.printStackTrace();
			}
			int alg = ((String) lengthDropDown.getSelectedItem()).equals("3DES") ? SymmetricKeyAlgorithmTags.TRIPLE_DES
					: SymmetricKeyAlgorithmTags.AES_128;
			try (FileOutputStream fos = new FileOutputStream(path)) {

				if (signingCheckBox.isSelected()) {
					// 1.auth
					msg = (new SignedFileProcessor()).signFile(msg, signingPassword.getText(),
							secretKeyList.get(signingKeyIndex));
					System.out.println(new SignedFileProcessor().verifyFile(msg, u));
				}

				if (zipCheckBox.isSelected()) {
					// 2.zipovanje
					msg = ZipRadix.compressMessage(msg);
				}

				if (encryptionCheckBox.isSelected()) {
					// 3.enkripcija
					msg = MessageEncryption.getInstance()
							.encryptMessage(publicEncryptionKeyList.get(encryptionKeyIndex), msg, alg);
				}

				if (radixCheckBox.isSelected()) {
					// 4.radix64
					// System.out.println("pre radix konverzije");
					// System.out.println(new String(msg));
					msg = ZipRadix.convertToRadix64(msg);
					// System.out.println("pre radix dekonverzije");
					// System.out.println(new String(msg));
				}

				fos.write(msg);

				// ZipRadix.decompressData(msg);
				// byte[] msg2 = ZipRadix.radixDeconversion(msg);
				// System.out.println("posle dekonverzije");
				// System.out.println(new String(msg2));
				showMessage("poruka je uspesno kreirana");
			} catch (IOException | PGPException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				showMessage("doslo je do greske prilikom kreiranja poruke");
			}
		});
		output.addActionListener(e -> {
			JFrame parentFrame = new JFrame();
			FileDialog fileChooser = new FileDialog(parentFrame);
			fileChooser.setTitle("odaberite putanju na kojoj ce se sacuvati poruka");
			fileChooser.setVisible(true);
			String filename = fileChooser.getDirectory() + fileChooser.getFile() + ".asc";
			outputPath.setText(filename);
		});

		inputButton.addActionListener(e -> {
			JFrame parentFrame = new JFrame();
			FileDialog fileChooser = new FileDialog(parentFrame);
			fileChooser.setTitle("odaberite putanju ulaznof fajla");
			fileChooser.setVisible(true);
			String filename = fileChooser.getDirectory() + fileChooser.getFile();
			inputPath.setText(filename);
		});

		back.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});

		buttonPanel.add(encrypt);
		buttonPanel.add(back);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(inputPanel);
		panel.add(outputPanel);
		panel.add(keyPanel);
		panel.add(checkBoxPanel);
		panel.add(algorithmPanel);
		panel.add(buttonPanel);
		updateInfo();
		setPanel(panel);
	}

	private void setEnabled(JPanel p, boolean enabled) {
		for (Component c : p.getComponents()) {
			c.setEnabled(enabled);
			if (c instanceof JPanel)
				setEnabled((JPanel) c, enabled);
		}
	}

	private void previous(JButton next, JButton prev, int size, char index) {
		int keyIndex = -1;
		if (index == 'E') {
			encryptionKeyIndex--;
			keyIndex = encryptionKeyIndex;
		} else {
			signingKeyIndex--;
			keyIndex = signingKeyIndex;
		}
		setButtons(next, prev, size, keyIndex);
		updateInfo();
	}

	private void next(JButton next, JButton prev, int size, char index) {
		int keyIndex = -1;
		if (index == 'E') {
			encryptionKeyIndex++;
			keyIndex = encryptionKeyIndex;
		} else {
			signingKeyIndex++;
			keyIndex = signingKeyIndex;
		}
		setButtons(next, prev, size, keyIndex);
		updateInfo();
	}

	private void setButtons(JButton next, JButton prev, int size, int keyIndex) {
		if (keyIndex > 0) {
			prev.setEnabled(true);
		} else {
			prev.setEnabled(false);
		}
		if (keyIndex == size - 1) {
			next.setEnabled(false);
		} else {
			next.setEnabled(true);
		}
	}

	private void updateEncryptionInfo() {
		if (encryptionKeyIndex >= publicSigningKeyList.size()) {
			encryptionKeyInfo.setText("");
			return;
		}
		PGPPublicKey key = publicSigningKeyList.get(encryptionKeyIndex);
		String secret = KeyFormatter.getInstance().publicKeyToString(key);
		encryptionKeyInfo.setText(secret);
	}

	private void updateSigningInfo() {
		if (signingKeyIndex >= secretKeyList.size()) {
			signingKeyInfo.setText("");
			return;
		}
		PGPSecretKey key = secretKeyList.get(signingKeyIndex);
		String secret = KeyFormatter.getInstance().secretKeyToString(key);
		signingKeyInfo.setText(secret);
	}

	private void updateInfo() {
		updateEncryptionInfo();
		updateSigningInfo();
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(getPanel(), message);
	}

}
