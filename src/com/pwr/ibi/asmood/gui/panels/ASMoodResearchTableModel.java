package com.pwr.ibi.asmood.gui.panels;

import java.util.LinkedList;
import java.util.List;

import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;

public class ASMoodResearchTableModel {

	public List<ASModel> allASDataModels;
	public List<ASModel> selectedASDataModels;
	
	public ASMoodResearchTableModel() {
		allASDataModels = new LinkedList<ASModel>();
		selectedASDataModels = new LinkedList<ASModel>();
	}
	
	public void addASModelToAll(ASModel model) {
		allASDataModels.add(model);
	}
	
	public void addASModelToSelected(ASModel model) {
		selectedASDataModels.add(model);
	}
	
	public void moveASModelToSelected(ASModel model) {
		allASDataModels.remove(model);
		selectedASDataModels.add(model);
		
		G.asManager.addASMood(model);
	}
	
	public void moveASModelToAll(ASModel model) {
		selectedASDataModels.remove(model);
		allASDataModels.add(model);
		
		G.asManager.removeASMood(model);
	}
	
	public void clear() {
		allASDataModels.clear();
		selectedASDataModels.clear();
	}
	
}
