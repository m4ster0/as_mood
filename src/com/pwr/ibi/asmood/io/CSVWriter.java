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
import com.pwr.ibi.asmood.utils.IPAddressUtil;

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
	
	public static void writeActiveASMood(String path, List<ASMood> asMoods) {
		try {
			FileWriter writer = new FileWriter(path);
			
			for(ASMood asMood: asMoods) {
				if(asMood.getAviableHosts().isEmpty())
					continue;
				
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
				
				String startIPRaw = IPAddressUtil.getLowestIPAddress(asMood.getAviableHosts());
				String endIPRaw = IPAddressUtil.getHighestIPAddress(asMood.getAviableHosts());
				System.out.println(asMood.getASModel().getASN() + " - lowestAddress: " + startIPRaw);
				System.out.println(asMood.getASModel().getASN() + " - highestAddress: " + endIPRaw);
				long startConvert = IPAddressUtil.convertIPAddressToLong(startIPRaw);
				long endConvert = IPAddressUtil.convertIPAddressToLong(endIPRaw);
				String startIP = IPAddressUtil.convertLongToIPAddress(startConvert);
				String endIP = IPAddressUtil.convertLongToIPAddress(endConvert);
				
				List<String> networks = IPAddressUtil.rangeToCIDRNetworks(startIP, endIP);
				
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
	
	public static void writeActiveASMoodHosts(String path, List<ASMood> asMoods) {
		try {
			FileWriter writer = new FileWriter(path);
			
			for(ASMood asMood: asMoods) {
				if(asMood.getAviableHosts().isEmpty())
					continue;
				
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
				
				line += ",subnets";
				for(ASSubnetModel subnet: asMood.getASModel().getSubnets())
					line += " " + subnet.getNetworkCIDRNotation();
				
				line += ",hosts";
				for(String host: asMood.getAviableHosts())
					line += " " + host;
				
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
