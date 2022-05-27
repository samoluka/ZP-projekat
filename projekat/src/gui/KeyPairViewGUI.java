package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyPair;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class KeyPairViewGUI extends GUI {

	private KeyPair pair;

	public KeyPairViewGUI(KeyPair pair, JPanel returnPanel, GUI parent) {
		setParent(parent);
		this.pair = pair;

		JPanel panel = new JPanel();

		Encoder b64 = Base64.getEncoder();

		String pub = new String(b64.encode(pair.getPublic().getEncoded()));
		String priv = new String(b64.encode(pair.getPrivate().getEncoded()));

		JButton pubButton = new JButton("pogledaj javni kljuc");
		JButton privButton = new JButton("Pogledaj privatni kljuc");
		JButton back = new JButton("nazad");

		JTextArea output = new JTextArea();
		output.setLineWrap(true);
		output.setEditable(false);
		output.setVisible(false);

		pubButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				output.setText(String.format("Public key:\n%s", pub));
				output.setVisible(true);
				// ((MainGui) getParent()).setInnerPanel(panel);
			}
		});

		privButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				output.setText(String.format("Private key:\n%s", priv));
				output.setVisible(true);
				// ((MainGui) getParent()).setInnerPanel(panel);
			}
		});

		back.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				((MainGui) getParent()).setInnerPanel(returnPanel);
			}
		});

		panel.setLayout(new BoxLayout(panel, 1));
		panel.add(pubButton);
		panel.add(privButton);
		panel.add(back);
		panel.add(output);

		setPanel(panel);
	}

}
