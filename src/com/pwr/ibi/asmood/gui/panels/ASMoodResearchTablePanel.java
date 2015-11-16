package com.pwr.ibi.asmood.gui.panels;

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import com.pwr.ibi.asmood.ASMoodManager;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASSubnetModel;

public class ASMoodResearchTablePanel extends ASMoodPanel implements ASMoodManagerListener {

	private static final long serialVersionUID = -7695209428054463449L;
	
	private ASMoodManager asManager;
	
	private JPanel content;
	private JTextField filter;
	
	private JTree allASDataTree;
	private JTree selectedASDataTree;
	
	public ASMoodResearchTablePanel(ASMoodManager asMoodManager) {
		super(asMoodManager);
		
		asMoodManager.addListener(this);
	}
	
	@Override
	protected void initUI() {
		
		content = new JPanel(new GridLayout(0, 2));
		
		allASDataTree = new JTree();
		allASDataTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
		selectedASDataTree = new JTree();
		
		JScrollPane allScrollPane = new JScrollPane();
		allScrollPane.add(allASDataTree);
		JScrollPane selectedScrollPane = new JScrollPane();
		selectedScrollPane.add(selectedASDataTree);
		
		content.add(allScrollPane);
		content.add(selectedScrollPane);
		add(content);
		
	}
	
	protected void populateAllASDataTree(List<ASModel> asModels) {
		DefaultMutableTreeNode asDataNode = null;
		DefaultMutableTreeNode asSubnetDataNode = null;
		
		for(ASModel model: asModels) {
			asDataNode = new DefaultMutableTreeNode(new ASDataTreeModel(model));
			
			for(ASSubnetModel subnet: model.getSubnets()) {
				asSubnetDataNode = new DefaultMutableTreeNode(new ASDataSubnetTreeModel(subnet));
				asDataNode.add(asSubnetDataNode);
			}
			
		}
	}

	@Override
	public void notifyManagerInitied(List<ASModel> asModels) {
		populateAllASDataTree(asModels);
	}
	

}

class ASDataTreeModel {
	
	private String asn;
	private String name;
	private String desc;
	
	public ASDataTreeModel(ASModel asModel)
	{
		this.asn = asModel.getASN();
		this.name = asModel.getName();
		this.desc = asModel.getDesc();
	}

	@Override
	public String toString() {
		return asn + ": " + name + " -- " + desc;
	}
	
	public String getASN() {
		return asn;
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
		return notationCIDR + ": " + desc;
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


