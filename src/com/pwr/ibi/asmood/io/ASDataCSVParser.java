package com.pwr.ibi.asmood.io;

import com.pwr.ibi.asmood.model.ASModel;
import com.pwr.ibi.asmood.model.ASRouteType;
import com.pwr.ibi.asmood.model.ASSubnetModel;

public class ASDataCSVParser extends CSVParser<ASModel>
{
	private static final String IMPORT_FIELD = "import";
	private static final String EXPORT_FIELD = "export";
	private static final String SUBNETS_FIELD = "subnets";
	
	private static final String DELIMETER = ",";
	
	public ASDataCSVParser(String filePath) {
		super(filePath, DELIMETER);
	}

	@Override
	protected ASModel parseLine(String[] lineFields) {
		
		if(lineFields.length < 6)
			return null;
		
		String asn = lineFields[0];
		String name = lineFields[1];
		String desc = lineFields[2];
		
		ASModel asModel = new ASModel(asn, name, desc);
		
		String[] importAS = lineFields[3].split(" ");
		if(importAS[0].equals(IMPORT_FIELD))
			for(int i = 1; i < importAS.length; i++)
				asModel.addASRoute(ASRouteType.IMPORT, importAS[i]);
		
		String[] exportAS = lineFields[4].split(" ");
		if(exportAS[0].equals(EXPORT_FIELD))
			for(int i = 1; i < exportAS.length; i++)
				asModel.addASRoute(ASRouteType.EXPORT, exportAS[i]);
		
		String[] subnets = lineFields[5].split(" ");
		if(subnets[0].equals(SUBNETS_FIELD))
			for(int i = 1; i < subnets.length; i += 2)
			{
				ASSubnetModel subnetModel = new ASSubnetModel(subnets[i], subnets[i + 1]);
				asModel.addSubnet(subnetModel);
			}
		
		
		return asModel;
	}
	
	
}
