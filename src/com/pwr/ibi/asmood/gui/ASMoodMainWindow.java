package com.pwr.ibi.asmood.gui;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import com.pwr.ibi.asmood.ASMoodManager;
import com.pwr.ibi.asmood.gui.panels.ASMoodExplorerPanel;
import com.pwr.ibi.asmood.gui.panels.ASMoodLogPanel;
import com.pwr.ibi.asmood.gui.panels.ASMoodResearchPanel;

public class ASMoodMainWindow extends JFrame {

	private static final String title = "ASMood Application";
	private static final int width = 1280;
	private static final int height = 720;
	
	private JPanel mainPanel;
	private JTabbedPane tabbedPane;
	
	private ASMoodResearchPanel researchPanel;
	private ASMoodExplorerPanel explorerPanel;
	private ASMoodLogPanel logPanel;
	
	public ASMoodMainWindow()
	{
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
		
		researchPanel = new ASMoodResearchPanel();
//		researchPanel.setPreferredSize(new Dimension(920, 480));
		researchPanel.setMinimumSize(new Dimension(900, 480));
		tabbedPane.addTab(ASMoodResearchPanel.TAB_TITLE, researchPanel);
		
		explorerPanel = new ASMoodExplorerPanel();
		tabbedPane.addTab(ASMoodExplorerPanel.TAB_TITLE, explorerPanel);
		
		logPanel = new ASMoodLogPanel();
		tabbedPane.addTab(ASMoodLogPanel.TAB_TITLE, logPanel);
		
		add(tabbedPane);
	}
	
}
