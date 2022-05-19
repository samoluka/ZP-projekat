package projekat;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class Main {
	public static void main(String[] args) {
		JFrame frame = new JFrame("My First GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(300, 300);
		JButton button = new JButton("STISNI ME FIKUSE");
		button.setBackground(Color.red);
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton) e.getSource();
				if (b.getText().equals("STISNI ME FIKUSE")) {
					b.setText("DOBRO GA STISCES");
					b.setBackground(Color.green);
				} else {
					b.setText("STISNI ME FIKUSE");
					b.setBackground(Color.red);
				}
			}
		});
		frame.getContentPane().add(button); // Adds Button to content pane of frame
		frame.setVisible(true);
		
		System.out.println("AJMO DONCIC");
	}
}
