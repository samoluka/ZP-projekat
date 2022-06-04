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
import org.bouncycastle.openpgp.PGPSecretKey;

import projekat.Keys;
import projekat.MessageDecryption;
import projekat.MessageEncryption;
import projekat.SignedFileProcessor;
import projekat.User;
import projekat.UserProvider;
import projekat.ZipRadix;

public class ReadMessageGUI extends GUI {

	private ArrayList<PGPSecretKey> secretEncryptionKeyList;
	private ArrayList<PGPSecretKey> secretSigningKeyList;
	private int keyIndex = 0;
	private JTextArea KeyInfo;

	public ReadMessageGUI(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();

		User u = UserProvider.getInstance().getCurrentUser();

		JButton input = new JButton("Putanja do poruke: ");
		JLabel inputPath = new JLabel();
		JPanel inputPanel = new JPanel();
		inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
		inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		inputPanel.add(input);
		inputPanel.add(inputPath);

		JLabel textLabel = new JLabel("Unesite sifru za desifrovanje:");
		JTextField password = new JTextField();
		password.setColumns(20);
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		textPanel.add(textLabel);
		textPanel.add(password);

		secretEncryptionKeyList = new ArrayList<>();
		secretSigningKeyList = new ArrayList<>();
		new Keys().getSecretRings(u).forEachRemaining(key -> {
			Iterator<PGPSecretKey> iter = key.getSecretKeys();
			secretSigningKeyList.add(iter.next());
			secretEncryptionKeyList.add(iter.next());
		});

		JButton encrypt = new JButton("desifruj poruku");
		JPanel encryptPanel = new JPanel();
		encryptPanel.add(encrypt);
		JButton back = new JButton("Nazad");
		JPanel backPanel = new JPanel();
		backPanel.add(back);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(encryptPanel);
		buttonPanel.add(backPanel);

		encrypt.addActionListener(e -> {
			String path = inputPath.getText();
			try (FileOutputStream fos = new FileOutputStream("izlaz.txt")) {
				byte[] msg = Files.readAllBytes(Paths.get(path));

				// 1.unradix
				msg = ZipRadix.radixDeconversion(msg);

				// 2.decrypt
				if (MessageEncryption.getInstance().isEncrypted(msg))
					msg = MessageDecryption.getInstance().decryptMessage(u, password.getText(), msg);

				// 3.unzip
				if (ZipRadix.checkIfCompressed(msg))
					msg = ZipRadix.decompressData(msg);

				// 4.veriySigning
				showMessage(new SignedFileProcessor().verifyFile(msg, u));

				// 5.unsign
				msg = new SignedFileProcessor().unsignedMessage(msg);

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

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(inputPanel);
		panel.add(textPanel);
		panel.add(buttonPanel);

		setPanel(panel);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(getPanel(), message);
	}
}
