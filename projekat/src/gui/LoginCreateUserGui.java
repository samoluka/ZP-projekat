package gui;

import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import projekat.UserProvider;

public class LoginCreateUserGui extends GUI {

	private int state;
	private JPanel usernamePanel;
	private JPanel passwordPanel;
	private JTextField username;
	private JTextField password;
	private JLabel passwordLabel;
	private Component usernameLabel;

	public LoginCreateUserGui(int state, GUI parent) {

		setParent(parent);

		this.state = state;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));

		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, 0));

		passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, 0));

		username = new JTextField(30);
		usernameLabel = new JLabel("korisnicko ime: ");
		usernamePanel.add(usernameLabel);
		usernamePanel.add(username);

		password = new JTextField(30);
		passwordLabel = new JLabel("sifra: ");
		passwordPanel.add(passwordLabel);
		passwordPanel.add(password);

		JButton actionButton = new JButton(state == 0 ? "Registracija" : "Login");
		JButton backButton = new JButton("Nazad");

		actionButton.addActionListener(e -> {
			if (state == 0) {
				registracija();
			} else {
				login();
			}
			((MainGui) getParent()).setInnerPanel((new FirstGui(getParent())).getPanel());
		});

		panel.add(usernamePanel);
		panel.add(passwordPanel);
		panel.add(actionButton);
		panel.add(backButton);

		setPanel(panel);

	}

	private void login() {

	}

	private void registracija() {
		String uName = username.getText();
		String pass = password.getText();
		UserProvider userProvider = UserProvider.getInstance();
		if (userProvider.createUser(uName, "Kurcina", pass)) {
			System.out.println("Uspesno kreiran korisnik");
			System.out.println(userProvider.getAllUsersAsString());
		} else {
			System.err.println("Korisnik vec postoji");
		}

	}
}
