package com.pwr.ibi.asmood.tools;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ping extends Tool<PingResult> {
	
	private static final String regex_response_pattern = "bytes from";
	private static final String regex_timeout_pattern = "timed out";

	private static final String COMMAND_PATH = "ping";
	private static final String[] OPTIONS = {"-c 3 -i 0.3"};
	
	private int packetLostCounter;
	private int packetCounter;
	
	public Ping(String hostname, String asn) {
		super(hostname, asn);
		
		this.command_path = COMMAND_PATH;
		this.options = OPTIONS;
		
		packetCounter = 0;
		packetLostCounter = 0;
	}


	@Override
	protected PingResult parseElement(String line) {
		PingResult result = null;
		String[] es = line.split(" ");
		
		if (line.contains(regex_response_pattern))
		{	
			List<String> fields = new ArrayList<String>(Arrays.asList(es));
			fields.removeAll(Arrays.asList("", null));
			
			result = new PingResult.PingResultBuilder(packetCounter, fields.get(3).replace(":", ""))
								   .timeToLive(fields.get(5).replace("ttl=", ""))
								   .roundTripTime(fields.get(6).replace("time=", ""))
								   .build();
			
			packetCounter++;
			
			return result;
		}
		
		if(line.contains(regex_timeout_pattern))
		{
			packetCounter++;
			packetLostCounter++;
		}
		
		return null;
	}
	
	public int getPacketLostCount() {
		return packetLostCounter;
	}
	
	public int getPacketReachedCount() {
		return packetCounter;
	}
	
	public boolean isHostReached()
	{
		return !results.isEmpty();
	}

}
