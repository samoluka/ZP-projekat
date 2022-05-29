package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import projekat.UserProvider;

public class FirstGui extends GUI {

	FirstGui(GUI parent) {
		JPanel panel = new JPanel();
		setParent(parent);

		JButton addUser = new JButton("Registracija");
		JButton login = new JButton("Login");
		JButton generateKeyPair = new JButton("Generisi novi par kljuca");
		JButton keyOverview = new JButton("Pregled kljuceva");

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

		panel.add(addUser);
		panel.add(login);
		panel.add(generateKeyPair);
		panel.add(keyOverview);

		setPanel(panel);

	}
}
