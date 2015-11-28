package com.pwr.ibi.asmood;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASSubnetModel;
import com.pwr.ibi.asmood.tasks.ASMoodHostSearchTask;
import com.pwr.ibi.asmood.tasks.ASMoodPingTask;
import com.pwr.ibi.asmood.tasks.ASMoodProgressListener;
import com.pwr.ibi.asmood.tasks.ASMoodTask;
import com.pwr.ibi.asmood.tasks.ASMoodTracerouteTask;
import com.pwr.ibi.asmood.tools.PingResult;

public class ASMood {
	
	public static final String HOST_SEARCH_ID = "host_search";
	public static final String PING_TIME_ID = "ping_time";
	public static final String PING_LOST_PACKETS_ID = "ping_lost_packets";
	public static final String TRACEROUTE_HOP_COUNT_ID = "traceroute_hop_count";
	public static final String TRACEROUTE_TIME_ID = "traceroute_time";
	
	public static final String PING_ID = "ping_task";
	public static final String TRACEROUTE_ID = "traceroute_task";
	
	public static final Map<Integer, String> researches = new HashMap<Integer, String>();
	{
		researches.put(0, PING_TIME_ID);
		researches.put(1, PING_LOST_PACKETS_ID);
		researches.put(2, TRACEROUTE_HOP_COUNT_ID);
		researches.put(3, TRACEROUTE_TIME_ID);
	}
	
//	private static final int DEFAULT_TIMEOUT = 300;
	public boolean initied;
	
	private Object lock;
	
	private ASModel asModel;
	private List<String> aviableHosts;
	
	private Map<String, Float> lostPackets;
	private Map<String, Float> hostPingTimes;
	
	private Map<String, Integer> tracerouteHopCounts;
	private Map<String, Float> tracerouteTimes;
	
	private ASMoodExplorationListener listener;
	private List<ASMoodTask> workingTasks;
	
	public ASMood(ASModel asModel)
	{
		lock = new Object();
		
		this.asModel = asModel;
		aviableHosts = new ArrayList<String>();
		
		lostPackets = new HashMap<String, Float>();
		hostPingTimes = new HashMap<String, Float>();
		
		tracerouteHopCounts = new HashMap<String, Integer>();
		tracerouteTimes = new HashMap<String, Float>();
		
		workingTasks = new ArrayList<ASMoodTask>();
		
		initied = true;
	}
	
	public void addListener(ASMoodExplorationListener listener) {
		this.listener = listener;
	}
	
	public void removeListener(ASMoodExplorationListener listener) {
		this.listener = null;
	}
	
	public void searchAviableHosts(int maxCount)
	{	
		ASMoodProgressListener<ASMoodHostSearchTask> searchCompletedListener = new ASMoodProgressListener<ASMoodHostSearchTask>() {
			@Override
			public void notifyTaskProgressChanged(ASMoodHostSearchTask task, float progress) {
				
				//System.out.println("TaskProgress: " + progress);
				synchronized (lock) {
					if(progress >= 1.0) {
						workingTasks.remove(task);
						task.removeListener(this);
						
						List<List<PingResult>> taskResults = task.getResults();
						
						for(List<PingResult> pingResults: taskResults)
							if(!pingResults.isEmpty())
								aviableHosts.add(pingResults.get(0).ipAddress);
							
						if(listener != null)
							listener.notifySearchHostsCompleted(ASMood.this);
						
						for(String host: aviableHosts)
							System.out.println("AviableHost: " + host);
						
						System.out.println("PingTask Completed");
					}
				}
			}
		};
		
		aviableHosts.clear();
		ASMoodHostSearchTask task = new ASMoodHostSearchTask(HOST_SEARCH_ID, asModel);
		workingTasks.add(task);
		task.addListener(searchCompletedListener);
		task.start(maxCount);
	}

	public List<String> getAviableHosts() {
		return aviableHosts;
	}
	
	public ASModel getASModel() {
		return asModel;
	}
	
	public void exploreASTraceroute() {
		ASMoodProgressListener<ASMoodTracerouteTask> tracerouteCompletedListener = new ASMoodProgressListener<ASMoodTracerouteTask>() {
			
			@Override
			public void notifyTaskProgressChanged(ASMoodTracerouteTask task, float progress) {
				
//				System.out.println(asModel.getASN() + " - " + TRACEROUTE_ID + " Progress: " + progress);
				
				if(progress >= 1.0) {
					workingTasks.remove(task);
					task.removeListener(this);
					
					tracerouteHopCounts.putAll(task.getHopCounts());
					tracerouteTimes.putAll(task.getAsRoundTripTimes());
						
					if(listener != null)
						listener.notifyASExplorationCompleted(TRACEROUTE_ID, ASMood.this);
					
//					System.out.println(asModel.getASN() + " - " + TRACEROUTE_ID + " completed");
				}
			}
		};
		
		tracerouteHopCounts.clear();
		tracerouteTimes.clear();
		ASMoodTracerouteTask task = new ASMoodTracerouteTask(TRACEROUTE_ID, asModel, aviableHosts);
		workingTasks.add(task);
		task.addListener(tracerouteCompletedListener);
		task.start(aviableHosts.size());
	}
	
	public void exploreASPing()
	{
		ASMoodProgressListener<ASMoodPingTask> pingCompletedListener = new ASMoodProgressListener<ASMoodPingTask>() {
			
			@Override
			public void notifyTaskProgressChanged(ASMoodPingTask task, float progress) {
				
//				System.out.println(asModel.getASN() + " - " + PING_ID + " Progress: " + progress);
				
				if(progress >= 1.0) {
					workingTasks.remove(task);
					task.removeListener(this);
					
					hostPingTimes.putAll(task.getTimes());
					lostPackets.putAll(task.getLostPackets());
						
					if(listener != null)
						listener.notifyASExplorationCompleted(PING_ID, ASMood.this);
					
//					System.out.println(asModel.getASN() + " - " + PING_ID + " completed");
				}
			}
		};
		
		hostPingTimes.clear();
		lostPackets.clear();
		ASMoodPingTask task = new ASMoodPingTask(PING_ID, asModel, aviableHosts);
		workingTasks.add(task);
		task.addListener(pingCompletedListener);
		task.start(aviableHosts.size());
	}
	
	public void terminateActiveTasks() {
		for (ASMoodTask task: workingTasks)
			task.terminate();
	}

	public Map<String, Float> getLostPackets() {
		return lostPackets;
	}

	public Map<String, Float> getHostPingTimes() {
		return hostPingTimes;
	}

	public Map<String, Integer> getTracerouteHopCounts() {
		return tracerouteHopCounts;
	}

	public Map<String, Float> getTracerouteTimes() {
		return tracerouteTimes;
	}
	
	
}
