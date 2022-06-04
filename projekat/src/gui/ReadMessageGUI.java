package gui;

import java.awt.FileDialog;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

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
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;

import projekat.Keys;
import projekat.MessageDecryption;
import projekat.SignedFileProcessor;
import projekat.User;
import projekat.UserProvider;
import projekat.ZipRadix;
import util.KeyFormatter;

public class ReadMessageGUI extends GUI {

	private ArrayList<PGPSecretKey> secretEncryptionKeyList;
	private ArrayList<PGPSecretKey> secretSigningKeyList;
	private int keyIndex = 0;
	private JTextArea KeyInfo;

	public ReadMessageGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();

		User u = UserProvider.getInstance().getCurrentUser();

		JLabel textLabel = new JLabel("Unesite sifru za desifrovanje");
		JTextField password = new JTextField();
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.add(textLabel);
		textPanel.add(password);

		JButton input = new JButton("Putanja do poruke: ");
		JLabel inputPath = new JLabel();
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.add(input);
		inputPanel.add(inputPath);

		JPanel KeyPanel = new JPanel();
		KeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		KeyPanel.setLayout(new BoxLayout(KeyPanel, BoxLayout.X_AXIS));
		KeyInfo = new JTextArea();
		JLabel KeyLabel = new JLabel("privatni kljuc info \t");
		KeyPanel.add(KeyLabel);
		KeyPanel.add(KeyInfo);
		KeyInfo.setEditable(false);

		JLabel originalMsg = new JLabel();

		secretEncryptionKeyList = new ArrayList<>();
		secretSigningKeyList = new ArrayList<>();
		new Keys().getSecretRings(u).forEachRemaining(key -> {
			Iterator<PGPSecretKey> iter = key.getSecretKeys();
			secretSigningKeyList.add(iter.next());
			secretEncryptionKeyList.add(iter.next());
		});

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton prev = new JButton("prethodni");
		prev.setEnabled(false);
		JButton next = new JButton("sledeci");
		buttonPanel.add(prev);
		buttonPanel.add(next);

		next.setEnabled(keyIndex < secretEncryptionKeyList.size() - 1);

		prev.addActionListener(e -> {
			keyIndex--;
			if (keyIndex < secretEncryptionKeyList.size() - 1) {
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
			if (keyIndex == secretEncryptionKeyList.size() - 1) {
				next.setEnabled(false);
			}
			updateInfo();
		});

		JButton encrypt = new JButton("desifruj poruku");
		JButton back = new JButton("Nazad");
		encrypt.addActionListener(e -> {
			String path = inputPath.getText();
			try (FileOutputStream fos = new FileOutputStream("izlaz.txt")) {
				byte[] msg = Files.readAllBytes(Paths.get(path));

				msg = ZipRadix.radixDeconversion(msg);

				// 1.auth

				// 2.zipovanje

				long KeyId = 1L;
				PGPSecretKey secretKey = new Keys().findPrivateRing(KeyId, u).getSecretKey();
				PGPPrivateKey privateKey = secretEncryptionKeyList.get(keyIndex).extractPrivateKey(
						new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build("luka123".toCharArray()));
				msg = MessageDecryption.getInstance().decryptMessage(privateKey, msg);

				if (ZipRadix.checkIfCompressed(msg))
					msg = ZipRadix.decompressData(msg);

				System.out.println(new SignedFileProcessor().verifyFile(msg, u));

				msg = new SignedFileProcessor().unsignedMessage(msg);

				// 3.enkripcija

				// 4.radix64

				fos.write(msg);
				showMessage("poruka je uspesno procitana");
			} catch (PGPException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				showMessage("doslo je do greske prilikom desifrovanja poruke");
			}
		});
		input.addActionListener(e -> {
			JFrame parentFrame = new JFrame();
			FileDialog fileChooser = new FileDialog(parentFrame);
			fileChooser.setTitle("odaberite putanju poruke");
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
		panel.add(textPanel);
		panel.add(inputPanel);
		panel.add(KeyPanel);
		panel.add(buttonPanel);
		panel.add(new JLabel("desifrovana poruka: "));
		panel.add(originalMsg);

		updateInfo();
		setPanel(panel);
	}

	private void updateInfo() {
		if (keyIndex >= secretSigningKeyList.size()) {
			KeyInfo.setText("");
			return;
		}
		PGPSecretKey key = secretSigningKeyList.get(keyIndex);
		String secret = KeyFormatter.getInstance().secretKeyToString(key);
		KeyInfo.setText(secret);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(getPanel(), message);
	}
}
