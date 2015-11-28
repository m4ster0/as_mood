package com.pwr.ibi.asmood.gui.panels;

import com.pwr.ibi.asmood.gui.panels.ASMoodResearchPanel.ASMoodProcessState;

public interface ASMoodResearchStateListener {

	public void notifyProcessStateChange(ASMoodProcessState state);
	
}
