package projekat;

import javax.swing.JFrame;

import gui.MainGui;

public class Main {

	public static void main(String[] args) {
		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);

		MainGui mainGui = new MainGui(frame);

		frame.add(mainGui.getPanel());
		frame.setVisible(true);

		System.out.println("AJMO DONCIC");
	}
}
