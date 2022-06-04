package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import projekat.UserProvider;

public class FirstGui extends GUI {

	FirstGui(GUI parent) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		setParent(parent);

		JButton addUser = new JButton("Registracija");
		JPanel addUserPanel = new JPanel();
		addUserPanel.add(addUser);

		JButton login = new JButton("Login");
		JPanel loginPanel = new JPanel();
		loginPanel.add(login);

		JButton generateKeyPair = new JButton("Generisi novi par kljuceva");
		JPanel generateKeyPairPanel = new JPanel();
		generateKeyPairPanel.add(generateKeyPair);

		JButton keyOverview = new JButton("Pregled kljuceva");
		JPanel keyOverviewPanel = new JPanel();
		keyOverviewPanel.add(keyOverview);

		JButton createButton = new JButton("kreiraj poruku");
		JPanel createPanel = new JPanel();
		createPanel.add(createButton);

		JButton decrypt = new JButton("procitaj poruku");
		JPanel decryptPanel = new JPanel();
		decryptPanel.add(decrypt);

		addUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new LoginCreateUserGui(0, getParent(), panel)).getPanel());
			}
		});

		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new LoginCreateUserGui(1, getParent(), panel)).getPanel());
			}
		});
		generateKeyPair.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				if (UserProvider.getInstance().getCurrentUser() != null)
					((MainGui) getParent()).setInnerPanel((new GenereteKeyPairGUI(panel, getParent())).getPanel());
				else
					System.err.println("Morate biti ulogovani za ovu akciju");
			}
		});

		keyOverview.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				if (UserProvider.getInstance().getCurrentUser() != null)
					((MainGui) getParent()).setInnerPanel((new ShowKeysGUI(panel, getParent())).getPanel());
				else
					System.err.println("Morate biti ulogovani za ovu akciju");
			}
		});
		createButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new CreateMessageGUI(panel, getParent())).getPanel());
			}
		});

		decrypt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new ReadMessageGUI(panel, getParent())).getPanel());
			}
		});

		panel.add(addUserPanel);
		panel.add(loginPanel);
		panel.add(generateKeyPairPanel);
		panel.add(keyOverviewPanel);
		panel.add(createPanel);
		panel.add(decryptPanel);

		setPanel(panel);

	}
}
