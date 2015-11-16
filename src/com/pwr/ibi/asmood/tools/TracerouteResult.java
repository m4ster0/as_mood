package com.pwr.ibi.asmood.tools;

import java.util.List;

public class TracerouteResult {
	
	public int index;
	public String hostname;
	public String ipAddress;
	public String asn;

	public List<String> pingTimes;
	
	private TracerouteResult(TraceResultBuilder builder)
	{
		this.index = builder.index;
		this.ipAddress = builder.ipAddress;
		this.hostname = builder.hostname;
		this.asn = builder.asn;
		this.pingTimes = builder.pingTimes;
	}
	
	public float getAveragePing() {
		float p = 0.0f;
		try {
			for (String st : pingTimes) {
				if (st.trim().length() > 0)
					p += Float.parseFloat(st);
			}
		} catch (NumberFormatException e) {
			System.out.println("NumberFormatException: " + pingTimes);
			p = 1.0f;
		}

		return p / pingTimes.size();
	}
	
	public static class TraceResultBuilder
	{
		private final int index;
		private final String ipAddress;
		
		private String hostname;
		private String asn;
		private List<String> pingTimes;
		
		public TraceResultBuilder(String index, String ipAddress)
		{
			this.index = Integer.parseInt(index);
			this.ipAddress = ipAddress;
		}
		
		public TraceResultBuilder hostname(String hostname)
		{
			this.hostname = hostname;
			return this;
		}
		
		public TraceResultBuilder asn(String asn)
		{
			this.asn = asn;
			return this;
		}
		
		public TraceResultBuilder pingTimes(List<String> pingTimes)
		{
			this.pingTimes = pingTimes;
			return this;
		}
		
		public TracerouteResult build()
		{
			return new TracerouteResult(this);
		}
	}
}