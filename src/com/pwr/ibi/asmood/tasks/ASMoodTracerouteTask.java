package com.pwr.ibi.asmood.tasks;

import java.util.List;

import com.pwr.ibi.asmood.tools.ToolThread;
import com.pwr.ibi.asmood.tools.Traceroute;
import com.pwr.ibi.asmood.tools.TracerouteResult;

public class ASMoodTracerouteTask extends ASMoodTask<Traceroute, TracerouteResult> {

	public ASMoodTracerouteTask(String taskId, List<String> hosts) {
		super(taskId, hosts);
	}

	@Override
	protected Traceroute createToolWorker(String host) {
		return new Traceroute(host);
	}

	@Override
	protected List<TracerouteResult> getToolWorkerResult(ToolThread toolThread) {
		if(toolThread instanceof Traceroute) {
			Traceroute traceroute = (Traceroute) toolThread;
			List<TracerouteResult> results = traceroute.getResults();
			
			return results.isEmpty() ? null : results;
		}
		
		return null;
	}
	
}
