package com.pwr.ibi.asmood.gui.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.pwr.ibi.asmood.ASMood;
import com.pwr.ibi.asmood.ASMoodManager;
import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASSubnetModel;

public class ASMoodResearchTablePanel extends ASMoodPanel implements ASMoodManagerListener {

	private static final long serialVersionUID = -7695209428054463449L;
	
	private enum ASDataMovementDirection {
		RIGHT,
		LEFT
	}
	
	private JPanel content;
	private JTextField filter;
	
	private JTree allASDataTree;
	private DefaultMutableTreeNode allASDataTreeRootNode;
	private DefaultTreeModel allASDataTreeModel;
	
	private JTree selectedASDataTree;
	private DefaultMutableTreeNode selectedASDataTreeRootNode;
	private DefaultTreeModel selectedASDataTreeModel;
	
	private JButton moveRightButton;
	private JButton moveLeftButton;
	
	private ASMoodResearchTableModel tableModel;
	
	public ASMoodResearchTablePanel(ASMoodResearchTableModel tableModel) {
		super();
		this.tableModel = tableModel;
		
		G.asManager.addListener(this);
	}
	
	@Override
	protected void initUI() {
		
		
		setLayout(new BorderLayout(10, 10));
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		content = new JPanel(gridBagLayout);
		
		allASDataTreeRootNode = new DefaultMutableTreeNode("Loaded AS");
		allASDataTree = new JTree(allASDataTreeRootNode);
		allASDataTree.setCellRenderer(new ASDataTreeCellRenderer());
		allASDataTree.setRootVisible(false);
		allASDataTreeModel = (DefaultTreeModel) allASDataTree.getModel();
		allASDataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		selectedASDataTreeRootNode = new DefaultMutableTreeNode("Selected AS");
		selectedASDataTree = new JTree(selectedASDataTreeRootNode);
		selectedASDataTree.setCellRenderer(new ASDataTreeCellRenderer());
		selectedASDataTree.setRootVisible(false);
		selectedASDataTreeModel = (DefaultTreeModel) selectedASDataTree.getModel();
		selectedASDataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		JScrollPane allScrollPane = new JScrollPane(allASDataTree);
		allScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollPane selectedScrollPane = new JScrollPane(selectedASDataTree);
		selectedScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		JPanel movementButtonsContainer = new JPanel();
		movementButtonsContainer.setLayout(new BoxLayout(movementButtonsContainer, BoxLayout.Y_AXIS));
		//movementButtonsContainer.setBorder(BorderFactory.createTitledBorder("M"));
		moveRightButton = new JButton(">");
		moveRightButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onASDataMovementButtonClicked(ASDataMovementDirection.RIGHT);
			}
		});
		
		moveLeftButton = new JButton("<");
		moveLeftButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onASDataMovementButtonClicked(ASDataMovementDirection.LEFT);
			}
		});
		
		moveRightButton.setAlignmentX(CENTER_ALIGNMENT);
		moveLeftButton.setAlignmentX(CENTER_ALIGNMENT);
		
		movementButtonsContainer.add(Box.createRigidArea(new Dimension(0, 180)));
		movementButtonsContainer.add(moveRightButton);
		movementButtonsContainer.add(Box.createVerticalGlue());
		movementButtonsContainer.add(moveLeftButton);
		movementButtonsContainer.add(Box.createRigidArea(new Dimension(0, 180)));
		
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 10, 5, 10);
		
		c.gridx = 0;
		c.gridwidth = 8;
		c.weightx = 0.8;
		c.weighty = 0.5;
		content.add(allScrollPane, c);
		c.gridx = 8;
		c.gridwidth = 2;
		c.weightx = 0.0;
		c.weighty = 0.5;
		content.add(movementButtonsContainer, c);
		c.gridx = 10;
		c.gridwidth = 8;
		c.weightx = 0.8;
		c.weighty = 0.5;
		content.add(selectedScrollPane, c);
		add(content, BorderLayout.CENTER);
	}
	
	protected void onASDataMovementButtonClicked(ASDataMovementDirection direction) {
		TreePath[] selectedPaths = null;
		switch(direction) {
			case RIGHT:
				selectedPaths = allASDataTree.getSelectionModel().getSelectionPaths();
				if(selectedPaths != null && selectedPaths.length > 0)
					moveASDataToSelected(selectedPaths);
				break;
			case LEFT:
				selectedPaths = selectedASDataTree.getSelectionModel().getSelectionPaths();
				if(selectedPaths != null && selectedPaths.length > 0)
					moveASDataToAll(selectedPaths);
				break;
		}
	}
	
	protected void moveASDataToSelected(final TreePath[] selectedPaths) {
		
		for(TreePath path: selectedPaths) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			Object nodeObject = selectedNode.getUserObject();
			if(nodeObject instanceof ASDataTreeModel) {
				ASDataTreeModel asData = (ASDataTreeModel) nodeObject;
				ASModel asModel = asData.getASModel();
				
				selectedNode.removeFromParent();
				
				addASDataNodeToParent(selectedASDataTreeRootNode, asModel, false);
				
				tableModel.moveASModelToSelected(asModel);
			}
			
		}
		allASDataTreeModel.reload(allASDataTreeRootNode);
		selectedASDataTreeModel.reload(selectedASDataTreeRootNode);
	}
	
	protected void moveASDataToAll(TreePath[] selectedPaths) {
		
		for(TreePath path: selectedPaths) {
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) path.getLastPathComponent();
			Object nodeObject = selectedNode.getUserObject();
			if(nodeObject instanceof ASDataTreeModel) {
				ASDataTreeModel asData = (ASDataTreeModel) nodeObject;
				ASModel asModel = asData.getASModel();
				
				addASDataNodeToParent(allASDataTreeRootNode, asModel, true);
				allASDataTreeModel.reload(allASDataTreeRootNode);
				
				selectedNode.removeFromParent();
				selectedASDataTreeModel.reload(selectedASDataTreeRootNode);
				
				tableModel.moveASModelToAll(asModel);
			}
			
		}
		
	}
	
	protected void populateAllASDataTree(List<ASModel> asModels) {
		DefaultMutableTreeNode asDataNode = null;
		DefaultMutableTreeNode asSubnetDataNode = null;
		
		for(ASModel model: asModels) {
			tableModel.addASModelToAll(model);
			addASDataNodeToParent(allASDataTreeRootNode, model, true);
		}
		System.out.println("Populate end");
		allASDataTreeModel.reload(allASDataTreeRootNode);
	}
	
	protected void addASDataNodeToParent(DefaultMutableTreeNode parent, ASModel model, boolean addSubnets) {
		ASDataTreeModel asModel = new ASDataTreeModel(model);
		DefaultMutableTreeNode asDataNode = new DefaultMutableTreeNode(new ASDataTreeModel(model));
		
		if(addSubnets)
			for(ASDataSubnetTreeModel subnetModel: asModel.getSubnetTreeModels()) {
				DefaultMutableTreeNode asSubnetDataNode = new DefaultMutableTreeNode(subnetModel);
				asDataNode.add(asSubnetDataNode);
			}
		
		parent.add(asDataNode);
	}

	@Override
	public void notifyManagerInitied(List<ASModel> asModels) {
		populateAllASDataTree(asModels);
	}

	@Override
	public void notifyManagerClear() {
		tableModel.clear();
		
		allASDataTreeRootNode.removeAllChildren();
		allASDataTreeModel.reload(allASDataTreeRootNode);
		
		selectedASDataTreeRootNode.removeAllChildren();
		selectedASDataTreeModel.reload(selectedASDataTreeRootNode);
	}

	@Override
	public void notifyHostSearchProgress(float progress, ASModel model, List<String> hosts) {
		System.out.println("ASMoodResearchTable -- HostSearch Progress: " + progress);
		addHostsToSelectedASDataNode(model, hosts);
	}
	
	private void addHostsToSelectedASDataNode(ASModel model, List<String> hosts) {
		System.out.println("ASMoodResearchTable -- Selected Node Children Count: " + selectedASDataTreeRootNode.getChildCount());
		for(int i = 0; i < selectedASDataTreeRootNode.getChildCount(); i++) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectedASDataTreeRootNode.getChildAt(i);
			ASModel nodeModel = ((ASDataTreeModel) node.getUserObject()).getASModel();
			System.out.println("ASMoodResearchTable -- Hosts are about to be added to selected Node");
			if(nodeModel == model) {
				System.out.println("ASMoodResearchTable -- Hosts added");
				node.removeAllChildren();
				
				for(String host: hosts) {
					DefaultMutableTreeNode hostNode = new DefaultMutableTreeNode(new ASDataHostAddressTreeModel(host));
					node.add(hostNode);
				}
				break;
			}	
		}
		
		selectedASDataTreeModel.reload(selectedASDataTreeRootNode);
	}

	@Override
	public void notifyManagerActiveInitied(List<ASMood> asMoods) {
		for(ASMood asMood: asMoods) {
			tableModel.addASModelToSelected(asMood.getASModel());
			addASDataNodeToParent(selectedASDataTreeRootNode, asMood.getASModel(), false);
			addHostsToSelectedASDataNode(asMood.getASModel(), asMood.getAviableHosts());
		}
		
	}
	
}

class ASDataTreeModel {
	
	private ASModel asModel;
	
	private String asn;
	private String name;
	private String desc;
	
	private List<ASDataSubnetTreeModel> subnetTreeModels;
	
	public ASDataTreeModel(ASModel asModel)
	{
		this.asModel = asModel;
		
		this.asn = asModel.getASN();
		this.name = asModel.getName();
		this.desc = asModel.getDesc();
		
		subnetTreeModels = new ArrayList<ASDataSubnetTreeModel>();
		initSubnetTreeModels(asModel.getSubnets());
	}
	
	private void initSubnetTreeModels(List<ASSubnetModel> subnets) {
		for (ASSubnetModel subnet: subnets)
			subnetTreeModels.add(new ASDataSubnetTreeModel(subnet));
	}

	@Override
	public String toString() {
		return asn + ": " + name + " -- " + desc;
	}
	
	public ASModel getASModel() {
		return asModel;
	}
	
	public String getASN() {
		return asn;
	}

	public List<ASDataSubnetTreeModel> getSubnetTreeModels() {
		return subnetTreeModels;
	}
	
	
	
}

class ASDataSubnetTreeModel {
	
	private String notationCIDR;
	private String desc;
	
	public ASDataSubnetTreeModel(ASSubnetModel asSubnetModel) {
		this.notationCIDR = asSubnetModel.getNetworkCIDRNotation();
		this.desc = asSubnetModel.getDesc();
	}
	
	@Override
	public String toString() {
		return "Network: " + notationCIDR;
	}
	
}

class ASDataHostAddressTreeModel {
	
	private String ipAddress;
	
	public ASDataHostAddressTreeModel(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	@Override
	public String toString() {
		return ipAddress;
	}
	
}

/**
 *	JTree components
 */

class ASDataTreeCellRenderer extends DefaultTreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		Object nodeObject = node.getUserObject();
		
		if(nodeObject instanceof ASDataSubnetTreeModel) {
			this.setEnabled(false);
			this.setFocusable(false);
		}
		
		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	}
	
	
	
}