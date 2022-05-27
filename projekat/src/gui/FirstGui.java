package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class FirstGui extends GUI {

	FirstGui(GUI parent) {
		JPanel panel = new JPanel();
		setParent(parent);

		JButton addUser = new JButton("Registracija");
		JButton login = new JButton("Login");
		JButton generateKeyPair = new JButton("Generisi novi par kljuca");

		addUser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new LoginCreateUserGui(0, getParent())).getPanel());
			}
		});

		login.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new LoginCreateUserGui(1, getParent())).getPanel());
			}
		});
		generateKeyPair.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				((MainGui) getParent()).setInnerPanel((new GenereteKeyPairGUI(getParent())).getPanel());
			}
		});

		panel.add(addUser);
		panel.add(login);
		panel.add(generateKeyPair);

		setPanel(panel);

	}
}
