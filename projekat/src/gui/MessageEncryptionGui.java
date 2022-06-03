package gui;

import java.awt.FileDialog;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.jcajce.JcePBESecretKeyDecryptorBuilder;
import org.bouncycastle.util.Strings;

import projekat.Keys;
import projekat.MessageEncryption;
import projekat.User;
import projekat.UserProvider;
import util.KeyFormatter;

public class MessageEncryptionGui extends GUI {

	private ArrayList<PGPSecretKeyRing> secretKeyList;
	private int keyIndex = 0;
	private JTextArea secretKeyInfo;

	public MessageEncryptionGui(JPanel returnPanel, GUI parent) {
		setParent(parent);
		JPanel panel = new JPanel();

		User u = UserProvider.getInstance().getCurrentUser();

		JLabel textLabel = new JLabel("Unesite poruku za sifrovanje");
		JTextField text = new JTextField();
		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.X_AXIS));
		textPanel.add(textLabel);
		textPanel.add(text);

		JButton output = new JButton("Putanja za cuvanje poruke: ");
		JLabel outputPath = new JLabel();
		JPanel outputPanel = new JPanel();
		outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.X_AXIS));
		outputPanel.add(output);
		outputPanel.add(outputPath);

		JPanel secretKeyPanel = new JPanel();
		secretKeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		secretKeyPanel.setLayout(new BoxLayout(secretKeyPanel, BoxLayout.X_AXIS));
		secretKeyInfo = new JTextArea();
		JLabel secretKeyLabel = new JLabel("privatni kljuc info \t");
		JButton secretKeyExportButton = new JButton("izvezi kljuc");
		secretKeyPanel.add(secretKeyLabel);
		secretKeyPanel.add(secretKeyInfo);
		secretKeyPanel.add(secretKeyExportButton);
		secretKeyInfo.setEditable(false);

		secretKeyList = new ArrayList<PGPSecretKeyRing>();
		new Keys().getSecretRings(u).forEachRemaining(key -> secretKeyList.add(key));

//		List<PGPPublicKey> pub = new ArrayList<>();
//		u.getPublicKeyRingCollection().getKeyRings().forEachRemaining(key -> {
//			Iterator<PGPPublicKey> iter = key.getPublicKeys();
//			iter.next();
//			pub.add(iter.next());
//		});

		JPanel algorithmPanel = new JPanel();
		algorithmPanel.setLayout(new BoxLayout(algorithmPanel, BoxLayout.X_AXIS));
		JLabel algorithm = new JLabel("Odaberi algoritam");
		String[] optionsToChoose = { "3DES", "AES" };
		JComboBox<String> lengthDropDown = new JComboBox<>(optionsToChoose);
		algorithmPanel.add(algorithm);
		algorithmPanel.add(lengthDropDown);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		JButton prev = new JButton("prethodni");
		prev.setEnabled(false);
		JButton next = new JButton("sledeci");
		buttonPanel.add(prev);
		buttonPanel.add(next);

		next.setEnabled(keyIndex < secretKeyList.size() - 1);

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

		JButton encrypt = new JButton("sifruj poruku");
		JButton back = new JButton("Nazad");
		encrypt.addActionListener(e -> {
			String path = outputPath.getText();
			byte[] msg = Strings.toByteArray(text.getText());
			int alg = ((String) lengthDropDown.getSelectedItem()).equals("3DES") ? SymmetricKeyAlgorithmTags.TRIPLE_DES
					: SymmetricKeyAlgorithmTags.AES_128;
			System.out.println(path);
			try (FileOutputStream fos = new FileOutputStream(path)) {
				Iterator<PGPPublicKey> iter = secretKeyList.get(keyIndex).getPublicKeys();
				iter.next();
				byte[] encrypted = MessageEncryption.getInstance().encryptMessage(iter.next(), msg, alg);
				fos.write(encrypted);
				showMessage("poruka je uspesno sifrovana");
				Iterator<PGPSecretKey> secretIter = secretKeyList.get(keyIndex).getSecretKeys();
				secretIter.next();
				PGPPrivateKey k = secretIter.next().extractPrivateKey(
						new JcePBESecretKeyDecryptorBuilder().setProvider("BC").build("luka123".toCharArray()));
				String original = new String(msg, StandardCharsets.UTF_8);
				System.out.println(original);
			} catch (PGPException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				showMessage("doslo je do greske prilikom sifrovanja poruke");
			}
		});
		output.addActionListener(e -> {
			JFrame parentFrame = new JFrame();
			FileDialog fileChooser = new FileDialog(parentFrame);
			fileChooser.setTitle("odaberite putanju na kojoj ce se sacuvati poruka");
			fileChooser.setVisible(true);
			String filename = fileChooser.getDirectory() + fileChooser.getFile() + ".encrypted";
			outputPath.setText(filename);
		});
		back.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});

		buttonPanel.add(encrypt);
		buttonPanel.add(back);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(textPanel);
		panel.add(outputPanel);
		panel.add(secretKeyPanel);
		panel.add(algorithmPanel);
		panel.add(buttonPanel);
		updateInfo();
		setPanel(panel);
	}

	private void updateInfo() {
		if (keyIndex >= secretKeyList.size()) {
			secretKeyInfo.setText("");
			return;
		}
		PGPPublicKey publicKey = secretKeyList.get(keyIndex).getPublicKey();
		PGPSecretKey secretKey = secretKeyList.get(keyIndex).getSecretKey();

		String pub = KeyFormatter.getInstance().publicKeyToString(publicKey);
		String secret = KeyFormatter.getInstance().secretKeyToString(secretKey);
		secretKeyInfo.setText(secret);
	}

	private void showMessage(String message) {
		JFrame f = new JFrame();
		JOptionPane.showMessageDialog(getPanel(), message);
	}

}
