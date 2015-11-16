package com.pwr.ibi.asmood.utils;

public class Common {

	public static boolean validateASN(String asn)
	{
		String regex = "AS[0-9]{1,}";
		
		return asn.matches(regex);
	}
	
}
