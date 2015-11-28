package com.pwr.ibi.asmood.tasks;

import java.util.List;

import com.pwr.ibi.asmood.G;
import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASSubnetModel;
import com.pwr.ibi.asmood.tools.Ping;

public class ASMoodHostSearchTask extends ASMoodPingTask {

	public ASMoodHostSearchTask(String taskId, ASModel asModel) {
		super(taskId, asModel, null);
	}

	@Override
	public void start(int maxResultCount) {
		this.maxResultCount = maxResultCount;
		G.logPanel.getTextArea().append(asModel.getASN() + " - Task start.\n");
		List<ASSubnetModel> subnets = asModel.getSubnets();
		
		for(ASSubnetModel subnet: subnets)
		{
			String[] networkAddresses = subnet.getAllAddresses();
			for(int i = 0; i < networkAddresses.length; i++)
			{
				Ping worker = createToolWorker(networkAddresses[i], asModel.getASN());
				worker.addListener(this);
				executorService.submit(worker);
			}
		}
	}
	
	
}
