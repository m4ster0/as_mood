package com.pwr.ibi.asmood.gui.panels;

import javax.swing.JPanel;

import com.pwr.ibi.asmood.ASMoodManager;

public abstract class ASMoodPanel extends JPanel {
	
	public static String TAB_TITLE = "Panel";
	
	public ASMoodPanel()
	{
		initUI();
	}
	
	protected abstract void initUI();

}
