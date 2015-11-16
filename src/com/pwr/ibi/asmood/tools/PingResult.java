package com.pwr.ibi.asmood.tools;


public class PingResult {
	
	public int index;
	public String ipAddress;
	public int timeToLive;
	public float roundTripTime;
	
	private PingResult(PingResultBuilder builder)
	{
		this.index = builder.index;
		this.ipAddress = builder.ipAddress;
		this.timeToLive = builder.timeToLive;
		this.roundTripTime = builder.roundTripTime;
	}
	
	public static class PingResultBuilder
	{
		private final int index;
		private final String ipAddress;
		
		private int timeToLive;
		private float roundTripTime;
		
		public PingResultBuilder(int index, String ipAddress)
		{
			this.index = index;
			this.ipAddress = ipAddress;
		}
		
		public PingResultBuilder timeToLive(String ttl)
		{
			this.timeToLive = Integer.parseInt(ttl);
			return this;
		}
		
		public PingResultBuilder roundTripTime(String rtt)
		{
			this.roundTripTime = Float.parseFloat(rtt);
			return this;
		}
		
		public PingResult build()
		{
			return new PingResult(this);
		}
	}

}
