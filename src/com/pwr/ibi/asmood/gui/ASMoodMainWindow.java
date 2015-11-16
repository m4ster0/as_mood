package com.pwr.ibi.asmood.gui;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.pwr.ibi.asmood.ASMoodManager;
import com.pwr.ibi.asmood.gui.panels.ASMoodExplorerPanel;
import com.pwr.ibi.asmood.gui.panels.ASMoodLogPanel;
import com.pwr.ibi.asmood.gui.panels.ASMoodResearchPanel;

public class ASMoodMainWindow extends JFrame {

	private static final String title = "ASMood Application";
	private static final int width = 960;
	private static final int height = 540;
	
	private ASMoodManager asMoodManager;
	
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;
	
	private ASMoodResearchPanel researchPanel;
	private ASMoodExplorerPanel explorerPanel;
	private ASMoodLogPanel logPanel;
	
	public ASMoodMainWindow(ASMoodManager asMoodManager)
	{
		this.asMoodManager = asMoodManager;
		
		initUI();
		initPanelsUI();
	}
	
	private void initUI()
	{
		setTitle(title);
		setSize(width, height);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		add(mainPanel);
	}
	
	private void initPanelsUI()
	{
		tabbedPane = new JTabbedPane();
		
		researchPanel = new ASMoodResearchPanel(asMoodManager);
		tabbedPane.addTab(ASMoodResearchPanel.TAB_TITLE, researchPanel);
		
		explorerPanel = new ASMoodExplorerPanel(asMoodManager);
		tabbedPane.addTab(ASMoodExplorerPanel.TAB_TITLE, explorerPanel);
		
		logPanel = new ASMoodLogPanel(asMoodManager);
		tabbedPane.addTab(ASMoodLogPanel.TAB_TITLE, logPanel);
		
		add(tabbedPane);
	}
	
}
