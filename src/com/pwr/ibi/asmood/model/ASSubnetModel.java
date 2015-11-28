package com.pwr.ibi.asmood.model;

import org.apache.commons.net.util.SubnetUtils;
import org.apache.commons.net.util.SubnetUtils.SubnetInfo;

public class ASSubnetModel implements DataModel {
	
	private String networkCIDRNotation;
	private String desc;
	private SubnetUtils subnetUtils;
	private SubnetInfo subnetInfo;
	
	public ASSubnetModel(String networkCIDRNotation, String desc)
	{
		this.networkCIDRNotation = networkCIDRNotation;
		this.desc = desc;
		
		this.subnetUtils = new SubnetUtils(networkCIDRNotation);
		this.subnetUtils.setInclusiveHostCount(true);
		this.subnetInfo = subnetUtils.getInfo();
	}
	
	public String getLowAddress()
	{
		return subnetInfo.getLowAddress();
	}
	
	public String getHighAddress()
	{
		return subnetInfo.getHighAddress();
	}
	
	public int getAddressCount()
	{
		return subnetInfo.getAddressCount();
	}
	
	public String[] getAllAddresses()
	{
		return subnetInfo.getAllAddresses();
	}
	
	public boolean containsAddress(String address) {
		return subnetInfo.isInRange(address);
	}

	public String getNetworkCIDRNotation() {
		return networkCIDRNotation;
	}

	public String getDesc() {
		return desc;
	}
}
