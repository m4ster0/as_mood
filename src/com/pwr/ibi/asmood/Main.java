package com.pwr.ibi.asmood;

import javax.swing.SwingUtilities;

import com.pwr.ibi.asmood.gui.ASMoodMainWindow;
import com.pwr.ibi.asmood.tools.Ping;
import com.pwr.ibi.asmood.tools.Traceroute;
import com.pwr.ibi.asmood.utils.IPAddressUtil;

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
				
				ASMoodMainWindow mainWindow = new ASMoodMainWindow();
				mainWindow.setVisible(true);
				
				//G.asManager.init("test_data/asn100-500.csv");
				
				String address = "192.168.0.1";
				long convert = IPAddressUtil.convertIPAddressToLong(address);
				System.out.println("Long: " + convert);
				System.out.println("String: " + IPAddressUtil.convertLongToIPAddress(convert));
				
				address = "192.167.0.1";
				convert = IPAddressUtil.convertIPAddressToLong(address);
				System.out.println("Long: " + convert);
				System.out.println("String: " + IPAddressUtil.convertLongToIPAddress(convert));
			}
		});
	}

}
