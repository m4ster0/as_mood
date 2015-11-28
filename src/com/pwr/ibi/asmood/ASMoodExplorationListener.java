package com.pwr.ibi.asmood;

public interface ASMoodExplorationListener {

	public void notifySearchHostsCompleted(ASMood asMood);
	
	public void notifyASExplorationCompleted(String taskId, ASMood asMood);
	
}
