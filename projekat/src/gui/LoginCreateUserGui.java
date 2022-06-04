package gui;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import projekat.HashText;
import projekat.User;
import projekat.UserProvider;

public class LoginCreateUserGui extends GUI {

	private int state;
	private JPanel usernamePanel;
	private JTextField username;
	private JTextField password;
	private JLabel passwordLabel;
	private JLabel usernameLabel;
	private JLabel messageLabel = new JLabel();

	public LoginCreateUserGui(int state, GUI parent, JPanel returnPanel) {

		setParent(parent);

		this.state = state;

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, 1));

		usernamePanel = new JPanel();
		usernamePanel.setLayout(new BoxLayout(usernamePanel, 0));

		username = new JTextField(30);
		usernameLabel = new JLabel("korisnicko ime: ");
		usernamePanel.add(usernameLabel);
		usernamePanel.add(username);

		JPanel passwordPanel = new JPanel();
		passwordPanel.setLayout(new BoxLayout(passwordPanel, 0));

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

		});
		backButton.addActionListener(e -> {
			((MainGui) getParent()).setInnerPanel(returnPanel);
		});

		panel.add(usernamePanel);
		panel.add(passwordPanel);
		panel.add(actionButton);
		panel.add(backButton);

		messageLabel.setForeground(Color.RED);
		panel.add(messageLabel);

		setPanel(panel);

	}

	private void login() {
		User u = UserProvider.getInstance().getUserByUsername(username.getText());
		if (u != null && okPassword(u, password.getText())) {
			messageLabel.setText("Uspesno logovanje");
			UserProvider.getInstance().setCurrentUser(u);
		} else {
			messageLabel.setText("Neuspesan login");
		}
	}

	private boolean okPassword(User u, String text) {
		String num = u.getPassword().substring(u.getUsername().length(), u.getUsername().length() + 5);
		String userHash = u.getUsername() + num + HashText.getSHA(text);
		return u.getPassword().equals(userHash);
	}

	private void registracija() {
		String uName = username.getText();
		String pass = password.getText();
		UserProvider userProvider = UserProvider.getInstance();
		if (userProvider.createUser(uName, pass)) {
			messageLabel.setText("Uspesno kreiran korisnik");

		} else {
			messageLabel.setText("Korisnik vec postoji");
		}

	}
}
