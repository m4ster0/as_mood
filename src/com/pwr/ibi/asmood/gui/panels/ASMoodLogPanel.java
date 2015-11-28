package com.pwr.ibi.asmood.gui.panels;

import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.pwr.ibi.asmood.ASMoodManager;
import com.pwr.ibi.asmood.G;

public class ASMoodLogPanel extends ASMoodPanel {
	
	public static String TAB_TITLE = "Logs";
	
	private static JTextArea textArea;

	public ASMoodLogPanel() {
		super();
		
		G.logPanel = this;
	}

	@Override
	protected void initUI() {
		setLayout(new GridLayout(1, 1));
		
		textArea = new JTextArea();
		JScrollPane areaScroll = new JScrollPane(textArea);
		
		add(areaScroll);
	}

	public JTextArea getTextArea() {
		return textArea;
	}
	
	public void append(String text) {
		textArea.append(text);
	}
}
