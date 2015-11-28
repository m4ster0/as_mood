package com.pwr.ibi.asmood.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pwr.ibi.asmood.ASMood;
import com.pwr.ibi.asmood.model.ASSubnetModel;

public class CSVWriter {
	
	public static void writeResults(ASMood asMood, Date date) {
		SimpleDateFormat sdFormat = new SimpleDateFormat("dd|MM|yyyy-HH:mm:ss");
		String formatedDate = sdFormat.format(date);
		
		try {
			String folderPath = "results/" + asMood.getASModel().getASN();
			File file = new File(folderPath);
			file.mkdirs();
			
			FileWriter writer = new FileWriter(folderPath + "/" + formatedDate + ".csv");
			
			String asn = asMood.getASModel().getASN();
			for(String host: asMood.getAviableHosts()) {
				Float pingTime = asMood.getHostPingTimes().get(host);
				Float lostPackets = asMood.getLostPackets().get(host);
				Integer asHopCounts = asMood.getTracerouteHopCounts().get(host);
				Float asPingTime = asMood.getTracerouteTimes().get(host);
				
				String pingTimeString = pingTime == null ? "" : pingTime.toString();
				String lostPacketsString = lostPackets == null ? "" : lostPackets.toString();
				String asHopCountsString = asHopCounts == null ? "" : asHopCounts.toString();
				String asPingTimeString = asPingTime == null ? "" : asPingTime.toString();
				
				String line = asn + "," + host +"," + pingTimeString + "," + lostPacketsString + "," + asHopCountsString + "," + asPingTimeString +"\n";
				writer.append(line);
			}
			
			writer.flush();
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void writeActiveASMood(List<ASMood> asMoods) {
		try {
			FileWriter writer = new FileWriter("test_data/active_as.csv");
			
			for(ASMood asMood: asMoods) {
				String line = "";
				
				line += asMood.getASModel().getASN();
				line += "," + asMood.getASModel().getName();
				line += "," + asMood.getASModel().getDesc();
				line += ",imports";
				for(String importASN: asMood.getASModel().getImportAS())
					line += " " + importASN;
				line += ",exports";
				for(String exportASN: asMood.getASModel().getExportAS())
					line += " " + exportASN;
				
				List<String> networks = new ArrayList<String>();
				for(ASSubnetModel subnet: asMood.getASModel().getSubnets())
					for(String host: asMood.getAviableHosts())
						if(subnet.containsAddress(host) && !networks.contains(subnet.getNetworkCIDRNotation())) {
							networks.add(subnet.getNetworkCIDRNotation());
							break;
						}
				
				line += ",subnets";
				for(String net: networks)
					line += " " + net;
				
				line += "\n";
				writer.append(line);
			}
			
			writer.flush();
		    writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
