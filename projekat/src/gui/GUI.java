package gui;

import javax.swing.JPanel;

public abstract class GUI {

	private JPanel panel;

	private GUI parent;

	public JPanel getPanel() {
		return panel;
	}

	public void setPanel(JPanel panel) {
		this.panel = panel;
	}

	public GUI getParent() {
		return parent;
	}

	public void setParent(GUI parent) {
		this.parent = parent;
	}

}
