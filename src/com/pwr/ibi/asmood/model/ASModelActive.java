package com.pwr.ibi.asmood.model;

import java.util.ArrayList;
import java.util.List;

public class ASModelActive implements DataModel {

	private ASModel asModel;
	private List<String> hosts;
	
	public ASModelActive(ASModel asModel) {
		this.asModel = asModel;
		this.hosts = new ArrayList<String>();
	}
	
	public void addHost(String host) {
		hosts.add(host);
	}

	public ASModel getAsModel() {
		return asModel;
	}

	public List<String> getHosts() {
		return hosts;
	}

}
