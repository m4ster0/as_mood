package com.pwr.ibi.asmood.tools;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class ToolThread implements Runnable {

	private final Set<ToolWorkCompletedListener> listeners;

	public ToolThread()
	{
		listeners = new CopyOnWriteArraySet<ToolWorkCompletedListener>();
	}
	
	public final void addListener(final ToolWorkCompletedListener listener)
	{
		listeners.add(listener);
	}
	
	public final void removeListener(final ToolWorkCompletedListener listener)
	{
		listeners.remove(listener);
	}
	
	private final void notifyListeners()
	{
		for(ToolWorkCompletedListener listener: listeners)
			listener.notifyToolWorkCompleted(this);
	}

	@Override
	public void run() {
		try {
			doTask();
		} finally {
			notifyListeners();
		}
	}
	
	public abstract void doTask();
	
}
