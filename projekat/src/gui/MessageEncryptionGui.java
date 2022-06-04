package gui;

import java.awt.FileDialog;
import java.io.FileOutputStream;
import java.io.IOException;
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
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.util.Strings;

import projekat.Keys;
import projekat.MessageEncryption;
import projekat.SignedFileProcessor;
import projekat.User;
import projekat.UserProvider;
import projekat.ZipRadix;
import util.KeyFormatter;

public class MessageEncryptionGui extends GUI {

	private ArrayList<PGPPublicKey> publicEncryptionKeyList;
	private ArrayList<PGPPublicKey> publicSigningKeyList;
	private ArrayList<PGPSecretKey> secretKeyList;
	private int keyIndex = 0;
	private JTextArea KeyInfo;

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

		JPanel KeyPanel = new JPanel();
		KeyPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		KeyPanel.setLayout(new BoxLayout(KeyPanel, BoxLayout.X_AXIS));
		KeyInfo = new JTextArea();
		JLabel KeyLabel = new JLabel("javni kljuc info \t");
		KeyPanel.add(KeyLabel);
		KeyPanel.add(KeyInfo);
		KeyInfo.setEditable(false);

		publicEncryptionKeyList = new ArrayList<PGPPublicKey>();
		publicSigningKeyList = new ArrayList<>();
		secretKeyList = new ArrayList<>();
		new Keys().getSecretRings(u).forEachRemaining(key -> {
			Iterator<PGPPublicKey> iter = key.getPublicKeys();
			Iterator<PGPSecretKey> secretIter = key.getSecretKeys();
			while (secretIter.hasNext()) {
				PGPSecretKey kSec = secretIter.next();
				// System.out.println(kSec.isMasterKey());
				if (kSec.isMasterKey()) {
					secretKeyList.add(kSec);
					break;
				}
			}
			publicSigningKeyList.add(iter.next());
			publicEncryptionKeyList.add(iter.next());
		});

		// ovo nije dobro
		// sifrovanje poruke uvezenim javnim kljucem ne radi
		new Keys().getImportedPublicRings(u).forEachRemaining(key -> {
			Iterator<PGPPublicKey> iter = key.getPublicKeys();
			PGPPublicKey pKey = iter.next();
			System.out.println(pKey.isEncryptionKey());
			publicSigningKeyList.add(pKey);
			pKey = iter.next();
			publicEncryptionKeyList.add(pKey);
//			while (iter) {
//				System.out.println(pKey.isEncryptionKey());
//			}
		});

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

		next.setEnabled(keyIndex < publicEncryptionKeyList.size() - 1);

		prev.addActionListener(e -> {
			keyIndex--;
			if (keyIndex < publicEncryptionKeyList.size() - 1) {
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
			if (keyIndex == publicEncryptionKeyList.size() - 1) {
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
			try (FileOutputStream fos = new FileOutputStream(path)) {
//				long id = 3649143097931425584L; // ID priv kljuca za autentikaciju
//				PGPSecretKey secretKey = ((new Keys()).findPrivateRing(id, u)).getSecretKey();
				// 1.auth
				msg = (new SignedFileProcessor()).signFile(msg, "luka123", secretKeyList.get(keyIndex));
				System.out.println(new SignedFileProcessor().verifyFile(msg, u));
//				// 2.zipovanje
				msg = ZipRadix.compressMessage(msg);
				// 3.enkripcija
				msg = MessageEncryption.getInstance().encryptMessage(publicEncryptionKeyList.get(keyIndex), msg, alg);
				// 4.radix64
				msg = ZipRadix.convertToRadix64(msg);
				fos.write(msg);
				showMessage("poruka je uspesno sifrovana");
			} catch (IOException | PGPException e1) {
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
			String filename = fileChooser.getDirectory() + fileChooser.getFile() + ".asc";
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
		panel.add(KeyPanel);
		panel.add(algorithmPanel);
		panel.add(buttonPanel);
		updateInfo();
		setPanel(panel);
	}

	private void updateInfo() {
		if (keyIndex >= publicSigningKeyList.size()) {
			KeyInfo.setText("");
			return;
		}
		PGPPublicKey key = publicSigningKeyList.get(keyIndex);
		String secret = KeyFormatter.getInstance().publicKeyToString(key);
		KeyInfo.setText(secret);
	}

	private void showMessage(String message) {
		JOptionPane.showMessageDialog(getPanel(), message);
	}

}
