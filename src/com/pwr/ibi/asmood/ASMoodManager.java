package com.pwr.ibi.asmood;

import java.util.ArrayList;
import java.util.List;

import com.pwr.ibi.asmood.gui.panels.ASMoodManagerListener;
import com.pwr.ibi.asmood.io.ASDataCSVParser;
import com.pwr.ibi.asmood.model.ASModel;

public class ASMoodManager {
	
	private List<ASModel> asModels;
	private boolean initied;
	
	private ASMoodManagerListener listener;
	
	public ASMoodManager()
	{
		asModels = new ArrayList<ASModel>();
		
		initied = false;
	}
	
	public void addListener(ASMoodManagerListener listener) {
		this.listener = listener;
	}
	
	public void removeListener(ASMoodManagerListener listener) {
		this.listener = null;
	}
	
	public void init(String filePath)
	{
		ASDataCSVParser parser = new ASDataCSVParser(filePath);
		
		parser.parse();
		asModels.addAll(parser.getResults());
		
		initied = true;
		
		if(listener != null)
			listener.notifyManagerInitied(asModels);
	}
	
	private ASModel getASModel(String asn)
	{
		for(ASModel model: asModels)
			if(model.getASN().equals(asn))
				return model;
		
		return null;
	}
	
	public void exploreAS(String asn)
	{
		ASModel asModel = getASModel(asn);
		ASMood asExplorer = new ASMood(asModel);
		
		asExplorer.init();
		asExplorer.searchAviableHosts(10);
	}

}
