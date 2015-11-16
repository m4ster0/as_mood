package com.pwr.ibi.asmood.tasks;

public interface ASMoodProgressListener<T extends ASMoodTask> {

	public void notifyTaskProgressChanged(T task, float progress);
	
}
