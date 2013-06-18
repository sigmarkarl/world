package org.simmi;

import javax.swing.JApplet;

public class FftApplet extends JApplet {
	public void init() {
		Fft fft = new Fft( this );
		this.add( fft );
		fft.init();
		fft.start();
	}
}
