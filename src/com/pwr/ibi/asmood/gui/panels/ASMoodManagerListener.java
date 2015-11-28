package com.pwr.ibi.asmood.gui.panels;

import java.util.List;

import com.pwr.ibi.asmood.model.ASModel;

public interface ASMoodManagerListener {
	public void notifyManagerInitied(List<ASModel> asModels);
	
	public void notifyHostSearchProgress(float progress, ASModel model, List<String> hosts);
	
	public void notifyManagerClear();
}
