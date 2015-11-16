package com.pwr.ibi.asmood;

import java.util.ArrayList;
import java.util.List;

import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASSubnetModel;
import com.pwr.ibi.asmood.tasks.ASMoodPingTask;
import com.pwr.ibi.asmood.tasks.ASMoodProgressListener;
import com.pwr.ibi.asmood.tools.PingResult;

public class ASMood {
	
	public static final String HOST_SEARCH_ID = "host_search";
	public static final String PING_TIME_ID = "ping_time";
	public static final String PING_LOST_PACKETS_ID = "ping_lost_packets";
	public static final String TRACEROUTE_HOP_COUNT_ID = "traceroute_hop_count";
	public static final String TRACEROUTE_TIME_ID = "traceroute_time";
	
//	private static final int DEFAULT_TIMEOUT = 300;
	
	private ASModel asModel;
	private List<String> asAddressPool;
	private List<String> aviableHosts;
	
	public ASMood(ASModel asModel)
	{
		this.asModel = asModel;
		asAddressPool = new ArrayList<String>();
		aviableHosts = new ArrayList<String>();
	}
	
	public void init()
	{
		List<ASSubnetModel> subnets = asModel.getSubnets();
		
		for(ASSubnetModel subnet: subnets)
		{
			String[] ipAddressess = subnet.getAllAddresses();
			for(String ip: ipAddressess)
				asAddressPool.add(ip);
		}
	}
	
	public void searchAviableHosts(int maxCount)
	{
		ASMoodProgressListener<ASMoodPingTask> listener = new ASMoodProgressListener<ASMoodPingTask>() {

			@Override
			public void notifyTaskProgressChanged(ASMoodPingTask task, float progress) {
				
				System.out.println("TaskProgress: " + progress);
				
				if(progress >= 1.0) {
					task.removeListener(this);
					
					List<List<PingResult>> taskResults = task.getResults();
					
					for(List<PingResult> pingResults: taskResults)
						if(!pingResults.isEmpty())
							aviableHosts.add(pingResults.get(0).ipAddress);
					
					for(String host: aviableHosts)
						System.out.println("AviableHost: " + host);
					
					System.out.println("PingTask Completed");
				}
			}
		};
		
		ASMoodPingTask task = new ASMoodPingTask(HOST_SEARCH_ID, asAddressPool);
		task.addListener(listener);
		task.start(maxCount);
	}

	public List<String> getAviableHosts() {
		return aviableHosts;
	}
	
	public void exploreASPingTime()
	{
		
	}
	
	public void exploreASPingLostPackets()
	{
		
	}
	
	public void exploreASTracerouteHopCount()
	{
		
	}
	
	public void exploreASTracerouteTime()
	{
		
	}
}
