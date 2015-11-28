package com.pwr.ibi.asmood.tasks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.tools.ToolThread;
import com.pwr.ibi.asmood.tools.ToolWorkCompletedListener;

public abstract class ASMoodTask<T extends ToolThread, V> implements ToolWorkCompletedListener {
	
	protected static final int maxConcurrentThreads = 8;
	
	protected Object lock = new Object();
	
	protected String taskId;
	protected ASModel asModel;
	protected volatile boolean taskCompleted;
	protected List<String> hosts;
	protected int maxResultCount;
	protected List<List<V>> results;
	protected volatile boolean taskNoMoreHosts;
	
	protected ExecutorService executorService;
	protected final List<ASMoodProgressListener> taskProgressListeners;
	
	public ASMoodTask(String taskId, ASModel asModel, List<String> hosts)
	{
		this.taskId = taskId;
		this.asModel = asModel;
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
		//System.out.println("Notify listeners");
		
		for(ASMoodProgressListener listener: taskProgressListeners)
			listener.notifyTaskProgressChanged(this, progress);
	}
	
	public void start(int maxResultCount) {
		this.maxResultCount = maxResultCount;
		
		G.logPanel.getTextArea().append(asModel.getASN() + " - Task start.\n");
		
		for(int i = 0; i < hosts.size(); i++)
		{
			T worker = createToolWorker(hosts.get(i), asModel.getASN());
			worker.addListener(this);
			executorService.submit(worker);
		}
	}
	
	public void terminate() {
		synchronized (lock) {
			notifyListeners((float) results.size() / maxResultCount);
			taskCompleted = true;
			executorService.shutdownNow();
		}
	}
	
	@Override
	public void notifyToolWorkCompleted(ToolThread toolThread) {
		synchronized (lock) {
			if(!taskCompleted) {
				List<V> result = getToolWorkerResult(toolThread);
				
				if(result != null) {
					results.add(result);
					//System.out.println(asModel.getASN() + " - Results count: " + results.size() + ", max: " + maxResultCount);
					if(results.size() >= maxResultCount) {
						G.logPanel.getTextArea().append(asModel.getASN() + " - Task shutDown.\n");
						System.out.println(asModel.getASN() + " - Task shutDown");
						taskCompleted = true;
						executorService.shutdownNow();
					}
					notifyListeners((float) results.size() / maxResultCount);
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
	
	protected abstract T createToolWorker(String host, String asn);

	protected abstract List<V> getToolWorkerResult(ToolThread toolThread);
	
}
