package com.pwr.ibi.asmood.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.pwr.ibi.asmood.tools.ToolThread;
import com.pwr.ibi.asmood.tools.ToolWorkCompletedListener;

public abstract class ASMoodTask<T extends ToolThread, V> implements ToolWorkCompletedListener {
	
	protected static final int maxConcurrentThreads = 100;
	
	protected Object lock = new Object();
	
	protected String taskId;
	protected boolean taskCompleted;
	protected List<String> hosts;
	protected int maxResultCount;
	protected List<List<V>> results;
	
	protected ExecutorService executorService;
	protected final List<ASMoodProgressListener> taskProgressListeners;
	
	public ASMoodTask(String taskId, List<String> hosts)
	{
		this.taskId = taskId;
		this.taskCompleted = false;
		this.hosts = hosts;
		this.results = new ArrayList<List<V>>();
		
		executorService = Executors.newFixedThreadPool(maxConcurrentThreads);
		taskProgressListeners = new ArrayList<ASMoodProgressListener>();
	}
	
	public final void addListener(ASMoodProgressListener listener)
	{
		taskProgressListeners.add(listener);
	}
	
	public final void removeListener(ASMoodProgressListener listner)
	{
		taskProgressListeners.remove(listner);
	}
	
	private final void notifyListeners(float progress)
	{
		System.out.println("Notify listeners");
		
		for(ASMoodProgressListener listener: taskProgressListeners)
			listener.notifyTaskProgressChanged(this, progress);
	}
	
	public void start(int maxResultCount)
	{
		this.maxResultCount = maxResultCount;
		
		for(int i = 0; i < hosts.size(); i++)
		{
			T worker = createToolWorker(hosts.get(i));
			worker.addListener(this);
			executorService.submit(worker);
		}
	}
	
	@Override
	public void notifyToolWorkCompleted(ToolThread toolThread) {
		List<V> result = getToolWorkerResult(toolThread);
		
		synchronized (lock) {
			if(!taskCompleted) {
				if(result != null) {
					results.add(result);
					notifyListeners((float) results.size() / maxResultCount);
				}
				
				if(results.size() >= maxResultCount)
				{
					taskCompleted = true;
					executorService.shutdownNow();
				}
			}
		}	
	}
	
	public List<List<V>> getResults() {
		return results;
	}
	
	public String getTaskId() {
		return taskId;
	}
	
	protected abstract T createToolWorker(String host);

	protected abstract List<V> getToolWorkerResult(ToolThread toolThread);
	
}
