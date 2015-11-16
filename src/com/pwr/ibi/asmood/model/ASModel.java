package com.pwr.ibi.asmood.model;

import java.util.ArrayList;
import java.util.List;

public class ASModel implements DataModel{
	
	private String asn;
	private String name;
	private String desc;
	
	private List<String> importAS;
	private List<String> exportAS;
	private List<ASSubnetModel> subnets;
	
	public ASModel(String asn, String name, String desc)
	{
		this.asn = asn;
		this.name = name;
		this.desc = desc;
		
		importAS = new ArrayList<String>();
		exportAS = new ArrayList<String>();
		
		subnets = new ArrayList<ASSubnetModel>();
	}
	
	public void addASRoute(ASRouteType routeType, String asn)
	{
		switch(routeType)
		{
			case IMPORT:
				importAS.add(asn);
				break;
			case EXPORT:
				exportAS.add(asn);
				break;
		}
	}

	public List<String> getImportAS() {
		return importAS;
	}

	public List<String> getExportAS() {
		return exportAS;
	}
	
	public void addSubnet(ASSubnetModel subnet)
	{
		subnets.add(subnet);
	}

	public List<ASSubnetModel> getSubnets() {
		return subnets;
	}

	public String getASN() {
		return asn;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}
}
