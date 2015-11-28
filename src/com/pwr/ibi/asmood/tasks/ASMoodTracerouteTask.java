package com.pwr.ibi.asmood.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.tools.ToolThread;
import com.pwr.ibi.asmood.tools.Traceroute;
import com.pwr.ibi.asmood.tools.TracerouteResult;

public class ASMoodTracerouteTask extends ASMoodTask<Traceroute, TracerouteResult> {
	
	protected Map<String, Integer> hopCounts;
	protected Map<String, Float> asRoundTripTimes;

	public ASMoodTracerouteTask(String taskId, ASModel asModel, List<String> hosts) {
		super(taskId, asModel, hosts);
		
		hopCounts = new HashMap<String, Integer>(hosts.size());
		asRoundTripTimes = new HashMap<String, Float>(hosts.size());
	}

	@Override
	protected Traceroute createToolWorker(String host, String asn) {
		return new Traceroute(host, asn);
	}

	@Override
	protected List<TracerouteResult> getToolWorkerResult(ToolThread toolThread) {
		if(toolThread instanceof Traceroute) {
			Traceroute traceroute = (Traceroute) toolThread;
			List<TracerouteResult> results = traceroute.getResults();
			
			String resultText = traceroute.isASReached() ? "host reached" : "host not reached";
//			System.out.println(asModel.getASN() + " - PING on " + ping.getHostname() + " result: " + resultText);
			G.logPanel.getTextArea().append(asModel.getASN() + " - TRACEROUTE on " + traceroute.getHostname() + "\t result: " + resultText + "\n");
			
			int hopCount = traceroute.getDestinationASHopCount();
			if(hopCount > 0)
				hopCounts.put(traceroute.getHostname(), hopCount);
			
			float times = traceroute.getASRoundTripTime();
			if(times != 0)
				asRoundTripTimes.put(traceroute.getHostname(), times);
			
			return results.isEmpty() ? null : results;
		}
		
		return null;
	}

	public Map<String, Integer> getHopCounts() {
		return hopCounts;
	}

	public Map<String, Float> getAsRoundTripTimes() {
		return asRoundTripTimes;
	}
	
	
	
}
