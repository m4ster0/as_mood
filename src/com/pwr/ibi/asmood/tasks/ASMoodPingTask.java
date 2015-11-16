package com.pwr.ibi.asmood.tasks;

import java.util.Collection;
import java.util.List;

import com.pwr.ibi.asmood.tools.Ping;
import com.pwr.ibi.asmood.tools.PingResult;
import com.pwr.ibi.asmood.tools.ToolThread;

public class ASMoodPingTask extends ASMoodTask<Ping, PingResult> {

	public ASMoodPingTask(String taskId, List<String> hosts) {
		super(taskId, hosts);
	}

	@Override
	protected Ping createToolWorker(String host) {
		return new Ping(host);
	}
	
	@Override
	protected List<PingResult> getToolWorkerResult(ToolThread toolThread) {
		if(toolThread instanceof Ping) {
			Ping ping = (Ping) toolThread;
			List<PingResult> results = ping.getResults();
			
			return results.isEmpty() ? null : results;
		}
		
		return null;
	}

}
