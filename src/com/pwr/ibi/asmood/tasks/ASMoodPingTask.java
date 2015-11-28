package com.pwr.ibi.asmood.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.tools.Ping;
import com.pwr.ibi.asmood.tools.PingResult;
import com.pwr.ibi.asmood.tools.ToolThread;

public class ASMoodPingTask extends ASMoodTask<Ping, PingResult> {
	
	protected Map<String, Float> lostPackets;
	protected Map<String, Float> pingTimes;

	public ASMoodPingTask(String taskId, ASModel asModel, List<String> hosts) {
		super(taskId, asModel, hosts);
		
		int initialCapacity = hosts == null ? 10 : hosts.size();
		lostPackets = new HashMap<String, Float>(initialCapacity);
		pingTimes = new HashMap<String, Float>(initialCapacity);
	}

	@Override
	protected Ping createToolWorker(String host, String asn) {
		return new Ping(host, asn);
	}
	
	@Override
	protected List<PingResult> getToolWorkerResult(ToolThread toolThread) {
		if(toolThread instanceof Ping) {
			Ping ping = (Ping) toolThread;
			List<PingResult> results = ping.getResults();
			
			String resultText = results.isEmpty() ? "no response" : "response";
//			System.out.println(asModel.getASN() + " - PING on " + ping.getHostname() + " result: " + resultText);
			G.logPanel.getTextArea().append(asModel.getASN() + " - PING on " + ping.getHostname() + "\t result: " + resultText + "\n");
			
			float accumulator = 0;
			for(PingResult pingResult: results)
				accumulator += pingResult.roundTripTime;
			
			float pingTime = accumulator / (float) results.size();
			pingTimes.put(ping.getHostname(), pingTime);
			
			float lostPacket = (float) ping.getPacketLostCount()/ping.getPacketReachedCount();
			lostPackets.put(ping.getHostname(), lostPacket);
			
			return results.isEmpty() ? null : results;
		}
		
		return null;
	}
	
	public Map<String, Float> getLostPackets() {
		return lostPackets;
	}

	public Map<String, Float> getTimes() {
		return pingTimes;
	}
}
