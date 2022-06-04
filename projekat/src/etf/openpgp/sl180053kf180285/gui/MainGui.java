package etf.openpgp.sl180053kf180285.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainGui extends GUI {

	JFrame parent;

	public MainGui(JFrame parent) {
		JPanel mainPanel = new JPanel();
//		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.setLayout(new GridBagLayout());
		mainPanel.add((new FirstGui(this)).getPanel(), new GridBagConstraints());

		setPanel(mainPanel);
		this.parent = parent;
	}

	public void setInnerPanel(JPanel innerPanel) {
		getPanel().removeAll();
		getPanel().add(innerPanel, new GridBagConstraints());
		// getPanel().revalidate();
		getPanel().validate();
		getPanel().repaint();
	}

}
