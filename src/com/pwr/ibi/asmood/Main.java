package com.pwr.ibi.asmood;

import javax.swing.SwingUtilities;

import com.pwr.ibi.asmood.gui.ASMoodMainWindow;
import com.pwr.ibi.asmood.tools.Ping;
import com.pwr.ibi.asmood.tools.Traceroute;

public class Main {

	public static void main(String[] args)
	{
//		Traceroute traceroute = new Traceroute("216.58.209.78");
//		traceroute.start();
		
//		Ping ping = new Ping("216.58.209.78");
//		ping.start();
		//manager.init("test_data/test.csv");
		//manager.exploreAS("AS8970");
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ASMoodManager manager = new ASMoodManager();
				
				ASMoodMainWindow mainWindow = new ASMoodMainWindow(manager);
				mainWindow.setVisible(true);
			}
		});
	}

}
