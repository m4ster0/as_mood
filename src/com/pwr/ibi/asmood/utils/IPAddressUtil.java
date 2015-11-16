package com.pwr.ibi.asmood.utils;

public class IPAddressUtil {

	public static long convertIPAddressToLong(String ipAddress)
	{
		long result = 0;
		
		String[] ipAddressInArray = ipAddress.split("\\.");
		if(ipAddressInArray.length != 4)
			throw new IllegalArgumentException("Wrong IP Address format.");
		
		int startIndex = 0;
		int endIndex = ipAddressInArray.length - 1;

		for (int i = endIndex; i >= startIndex; i--)
		{
			long ip = Long.parseLong(ipAddressInArray[endIndex - i]);
			result |= ip << (i * 8);
		}

		return result;
	}
	
	public static String convertLongToIPAddress(long ip)
	{
		return ((ip >> 24) & 0xFF) + "." 
			 + ((ip >> 16) & 0xFF) + "." 
			 + ((ip >> 8) & 0xFF) + "." 
			 + (ip & 0xFF);
	}
	
}
