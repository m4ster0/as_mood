package com.pwr.ibi.asmood.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Traceroute extends Tool<TracerouteResult>{
	
	private static final String COMMAND_PATH = "traceroute";
	private static final String[] OPTIONS = {"-A"};
	
	public Traceroute(String hostname, String asn)
	{
		super(hostname, asn);
		
		this.command_path = COMMAND_PATH;
		this.options = OPTIONS;
	}
	
	protected TracerouteResult parseElement(String line) {
		TracerouteResult result = null;
		String[] es = line.split(" ");

		Pattern timeoutLinePattern = Pattern.compile("[*]\\s");
		Matcher timeoutLine = timeoutLinePattern.matcher(line);

		Pattern correctLinePattern = Pattern.compile("^[\\d]*\\s.*$");
		Matcher correctLine = correctLinePattern.matcher(line);

		if (timeoutLine.find() == false && correctLine.matches() == true)
		{
			List<String> fields = new ArrayList<String>(Arrays.asList(es));
			fields.removeAll(Arrays.asList("", null));
			List<String> pings = new ArrayList<String>(3);
			
			int pingStartIndex = 4;
			for(int i = pingStartIndex; i < fields.size(); i++)
				if(fields.get(i).equals("ms"))
					pings.add(fields.get(i - 1));
			
			result = new TracerouteResult.TraceResultBuilder(fields.get(0), fields.get(2).replaceAll("\\(|\\)", ""))
								.hostname(fields.get(1))
								.asn(fields.get(3).replaceAll("\\[|\\]", "").split("/")[0])
								.pingTimes(pings)
								.build();

			return result;
		}

		return null;
	}
	
	public boolean isHostReached() {
		return results.get(results.size() - 1).ipAddress.equals(hostname);
	}
	
	public boolean isASReached() {
		return results.get(results.size() - 1).asn.equals(asn);
	}
	
	public int getDestinationASHopCount() {
		if(isASReached()) {
			int hopCounter = 0;
			String destASN = results.get(results.size() - 1).asn;
			for(TracerouteResult result: results)
				if(result.asn.equals(destASN))
					hopCounter++;
			
			return hopCounter;
		}
		
		return -1;
	}
	
	public float getASRoundTripTime() {
		if(isASReached() && results.size() > 1) {
			float destTime = results.get(results.size() - 1).getAveragePing();
			for(int hostIndex = results.size() - 2; hostIndex >= 0 ; hostIndex--)
				if(!results.get(hostIndex).asn.equals(asn)) {
					float diff = destTime - results.get(hostIndex + 1).getAveragePing();
					
					return diff != 0f ? diff : 0f;
				}
					
		}
		
		return 0f;
	}
}
