package com.pwr.ibi.asmood.gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.bric.swing.JThrobber;
import com.pwr.ibi.asmood.ASMood;
import com.pwr.ibi.asmood.G;

public class ASMoodResearchPanel extends ASMoodPanel implements ASMoodResearchStateListener {
	
	private static final long serialVersionUID = 5158491118692380602L;
	
	public static final String TAB_TITLE = "Research";
	
	private static String hostSearchBtnText = "Search";
	private static String researchBtnText = "Research";
	private static String stopBtnText = "Stop";
	
	public enum ASMoodProcessState {
		HostSearchProcess,
		ExploreProcess,
		IdleProcess
	}
	
	public ASMoodProcessState processState;
	
	private ASMoodResearchTablePanel tablePanel;
	private ASMoodResearchTableModel tableModel;
	
	private JPanel fileManagerPanel;
	private JTextField filePathTextField;
	private JButton fileChooserButton;
	private JButton loadDataButton;
	private JButton clearDataButton;
	private JButton saveActiveDataButton;
	
	private JTextField hostNumberTextField;
	private JTextField timeoutTextField;
	private JButton searchHostsButton;
	
	private JButton researchButton;
	private JCheckBox pingTimeCheckBox;
	private JCheckBox pingLostPacketsCheckBox;
	private JCheckBox tracerouteHopCountTimeCheckBox;
	private JCheckBox tracerouteTimeCheckBox;
	private JCheckBox[] researchCheckbox;
	private List<String> selectedResearch;
	
	private JPanel searchHostsProcessPanel;
	private JPanel researchProcessPanel;

	public ASMoodResearchPanel() {
		tableModel = new ASMoodResearchTableModel();
		processState = ASMoodProcessState.IdleProcess;
		initUI();
		
		G.researchPanel = this;
		G.asManager.addStateListener(this);
	}

	@Override
	protected void initUI() {
		
		setLayout(new BorderLayout(10, 10));
		
		tablePanel = new ASMoodResearchTablePanel(tableModel);
		add(tablePanel, BorderLayout.CENTER);
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(1240, 140));
		add(bottomPanel, BorderLayout.PAGE_END);
		
		bottomPanel.setLayout(new GridBagLayout());
		
		initFileManagerPanel(bottomPanel);
		initHostSearchPanel(bottomPanel);
		initASResearchPanel(bottomPanel);
		
	}
	
	protected void initFileManagerPanel(JPanel bottomPanel) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 5, 2, 5);
		
		fileManagerPanel = new JPanel();
		fileManagerPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 0.6;
		c.weighty = 0.5;
		bottomPanel.add(fileManagerPanel, c);
		
		fileManagerPanel.setLayout(new GridBagLayout());
		Insets normalInset = new Insets(5, 5, 5, 5);
		Insets leftInset = new Insets(5, 80, 5, 10);
		Insets rightInset = new Insets(5, 10, 5, 25);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		filePathTextField = new JTextField();
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 6;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		fileManagerPanel.add(filePathTextField, c);
		
		fileChooserButton = new JButton("File...");
		c.insets = rightInset;
		c.gridx = 6;
		c.gridwidth = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 0.3;
		c.weighty = 0.5;
		fileManagerPanel.add(fileChooserButton, c);
		
		JPanel placeholderPanel = new JPanel();
		placeholderPanel.setLayout(new GridLayout(1, 1));
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 8;
		c.gridy = 1;
		c.gridheight = 1;
		c.weightx = 1.0;
		c.weighty = 0.5;
		fileManagerPanel.add(placeholderPanel, c);
		
		loadDataButton = new JButton("Load");
		c.insets = leftInset;
		c.gridx = 2;
		c.gridwidth = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		fileManagerPanel.add(loadDataButton, c);
		
		clearDataButton = new JButton("Clear");
		c.insets = normalInset;
		c.gridx = 4;
		c.gridwidth = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		fileManagerPanel.add(clearDataButton, c);
		
		saveActiveDataButton = new JButton("Save");
		c.insets = rightInset;
		c.gridx = 6;
		c.gridwidth = 2;
		c.gridy = 2;
		c.gridheight = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		fileManagerPanel.add(saveActiveDataButton, c);
		
		fileChooserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
				int result = fileChooser.showOpenDialog(ASMoodResearchPanel.this);
				if(result == JFileChooser.APPROVE_OPTION)
					filePathTextField.setText(fileChooser.getSelectedFile().getPath());
			}
		});
		
		loadDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				File file = new File(filePathTextField.getText());
				if(file.exists() && file.isFile())
					G.asManager.init(filePathTextField.getText());
			}
		});
		
		clearDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				G.asManager.clear();
			}
		});
		
		saveActiveDataButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				G.asManager.saveActiveASData();
			}
		});
	}
	
	protected void initHostSearchPanel(JPanel bottomPanel) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 5, 2, 5);
		
		JPanel hostSearchPanel = new JPanel();
		hostSearchPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		c.gridx = 3;
		c.gridwidth = 1;
		c.weightx = 0.2;
		c.weighty = 0.5;
		bottomPanel.add(hostSearchPanel, c);
		
		hostSearchPanel.setLayout(new GridBagLayout());
		
		Insets normalInset = new Insets(5, 5, 5, 5);
		Insets textFieldInset = new Insets(5, 5, 5, 20);
		Insets buttonInset = new Insets(5, 60, 5, 60);
		
		c.fill = GridBagConstraints.BOTH;
		
		JLabel hostNumberLabel = new JLabel("AS Hosts:", SwingConstants.RIGHT);
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 0.2;
		c.weighty = 0.5;
		hostSearchPanel.add(hostNumberLabel, c);
		
		hostNumberTextField = new JTextField("16");
		c.insets = textFieldInset;
		c.gridx = 2;
		c.gridwidth = 3;
		c.gridy = 0;
		c.weightx = 0.8;
		c.weighty = 0.5;
		hostSearchPanel.add(hostNumberTextField, c);
		
		JLabel timeoutLabel = new JLabel("Timeout:", SwingConstants.RIGHT);
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 1;
		c.weightx = 0.2;
		c.weighty = 0.5;
		hostSearchPanel.add(timeoutLabel, c);
		
		timeoutTextField = new JTextField("300 ms");
		c.insets = textFieldInset;
		c.gridx = 2;
		c.gridwidth = 3;
		c.gridy = 1;
		c.weightx = 0.8;
		c.weighty = 0.5;
		hostSearchPanel.add(timeoutTextField, c);
		
		searchHostsProcessPanel = new JPanel();
		searchHostsProcessPanel.setLayout(new GridLayout(1, 1));
		JPanel panel = new JPanel();
		searchHostsProcessPanel.add(panel);
		JThrobber throbber = new JThrobber();
		JLabel text = new JLabel("Processing...");
		panel.add(throbber);
		panel.add(text);
		setPanelChildrenVisible(searchHostsProcessPanel, false);
		
		c.insets = buttonInset;
		c.gridx = 0;
		c.gridwidth = 5;
		c.gridy = 2;
		c.weightx = 0.0;
		c.weighty = 0.5;
		hostSearchPanel.add(searchHostsProcessPanel, c);
		
		searchHostsButton = new JButton("Search");
		c.insets = buttonInset;
		c.gridx = 0;
		c.gridwidth = 5;
		c.gridy = 3;
		c.weightx = 0.0;
		c.weighty = 0.5;
		hostSearchPanel.add(searchHostsButton, c);
		
		searchHostsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switch (processState) {
				case HostSearchProcess:
					G.asManager.terminateActive();
					break;
				default:
					int hostNum = Integer.parseInt(hostNumberTextField.getText().trim());
					G.asManager.searchForHosts(hostNum);
					System.out.println("Start searching for aviable hosts");
					break;
				}
			}
		});
		
	}
	
	protected void initASResearchPanel(JPanel bottomPanel) {
		GridBagConstraints c = new GridBagConstraints();
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(2, 5, 2, 5);
		
		JPanel asResearchPanel = new JPanel();
		asResearchPanel.setBorder(BorderFactory.createLineBorder(Color.darkGray, 1));
		c.gridx = 4;
		c.gridwidth = 2;
		c.weightx = 0.1;
		c.weighty = 0.5;
		bottomPanel.add(asResearchPanel, c);
		
		researchCheckbox = new JCheckBox[4];
		selectedResearch = new ArrayList<String>();
		asResearchPanel.setLayout(new GridBagLayout());
		
		Insets normalInset = new Insets(5, 20, 5, 20);
		Insets buttonInset = new Insets(5, 100, 5, 100);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		
		researchCheckbox[0] = new JCheckBox("AS Response Time", true);
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		asResearchPanel.add(researchCheckbox[0], c);
		
		researchCheckbox[1] = new JCheckBox("AS Lost Packets", true);
		c.insets = normalInset;
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		c.weighty = 0.5;
		asResearchPanel.add(researchCheckbox[1], c);
		
		researchCheckbox[2] = new JCheckBox("AS Hop Count", true);
		c.insets = normalInset;
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		asResearchPanel.add(researchCheckbox[2], c);
		
		researchCheckbox[3] = new JCheckBox("AS RTT", true);
		c.insets = normalInset;
		c.gridx = 1;
		c.gridwidth = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		c.weighty = 0.5;
		asResearchPanel.add(researchCheckbox[3], c);
		
		researchProcessPanel = new JPanel();
		researchProcessPanel.setLayout(new GridLayout(1, 2));
//		researchProcessPanel.setVisible(false);
		JPanel panel = new JPanel();
		researchProcessPanel.add(panel);
		JThrobber throbber = new JThrobber();
		JLabel text = new JLabel("Processing...");
		panel.add(throbber);
		panel.add(text);
		setPanelChildrenVisible(researchProcessPanel, false);
		
		c.insets = buttonInset;
		c.gridx = 0;
		c.gridwidth = 5;
		c.gridy = 2;
		c.weightx = 0.0;
		c.weighty = 0.5;
		asResearchPanel.add(researchProcessPanel, c);
		
		researchButton = new JButton("Research");
		c.insets = buttonInset;
		c.gridx = 0;
		c.gridwidth = 2;
		c.gridy = 3;
		c.weightx = 1.0;
		c.weighty = 0.5;
		asResearchPanel.add(researchButton, c);
		
		researchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				switch (processState) {
				case ExploreProcess:
					G.asManager.terminateActive();
					break;
				default:
					selectedResearch.clear();
					for(int i = 0; i < researchCheckbox.length; i++)
						if(researchCheckbox[i].isSelected())
							selectedResearch.add(ASMood.researches.get(i));
					
					if(!selectedResearch.isEmpty())
						G.asManager.exploreAS(selectedResearch);
					
					System.out.println("Start researching aviable hosts");
					break;
				}
			}
		});
	}

	@Override
	public void notifyProcessStateChange(ASMoodProcessState state) {
		processState = state;
		
		switch(processState) {
		case HostSearchProcess:
			searchHostsButton.setText(stopBtnText);
//			searchHostsProcessPanel.setVisible(true);
			setPanelChildrenVisible(searchHostsProcessPanel, true);
			researchButton.setEnabled(false);
			loadDataButton.setEnabled(false);
			clearDataButton.setEnabled(false);
			break;
		case ExploreProcess:
			researchButton.setText(stopBtnText);
//			researchProcessPanel.setVisible(true);
			setPanelChildrenVisible(researchProcessPanel, true);
			searchHostsButton.setEnabled(false);
			loadDataButton.setEnabled(false);
			clearDataButton.setEnabled(false);
			break;
		case IdleProcess:
		default:
			searchHostsButton.setText(hostSearchBtnText);
			searchHostsButton.setEnabled(true);
//			searchHostsProcessPanel.setVisible(false);
			setPanelChildrenVisible(searchHostsProcessPanel, false);
			
			researchButton.setText(researchBtnText);
			researchButton.setEnabled(true);
			setPanelChildrenVisible(researchProcessPanel, false);
			
			loadDataButton.setEnabled(true);
			clearDataButton.setEnabled(true);
			break;
		}
	}
	
	private void setPanelChildrenVisible(JPanel panel, boolean visible) {
		Component[] components = panel.getComponents();
		
		for(int i = 0; i < components.length; i++)
			components[i].setVisible(visible);
	}

}
