package com.example.progetto.angelo.rosa.main;

import java.awt.EventQueue;

import com.example.progetto.angelo.rosa.view.StudentSwingView;

/**
 * Simple app accessing MongoDB.
 */
public class Main {
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					StudentSwingView frame = new StudentSwingView();
					frame.setTitle("Student View");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
