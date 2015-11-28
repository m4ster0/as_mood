package com.pwr.ibi.asmood.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public abstract class Tool<T> extends ToolThread {

	protected String command_path = "";
	protected String[] default_options = {};
	
	protected String[] options;
	protected String hostname;
	protected String asn;
	
	protected List<T> results;
	
	public boolean started = false;
	
	public Tool(String hostname, String asn)
	{
		super();
		
		this.options = default_options;
		this.hostname = hostname;
		this.asn = asn;
		
		results = new ArrayList<T>();
	}
	
	@Override
	public void doTask() {
		started = true;
		
		try {
			String line;
			String command = command_path + " " + getCommandOptions() + " " + hostname;
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			while ((line = input.readLine()) != null) 
			{
				T result = parseElement(line.trim());
				
				if(result != null)
					results.add(result);
			}
			
			input.close();
		}
		catch (Exception err) {
			err.printStackTrace();
		}
	}
	
	public List<T> getResults() {
		return results;
	}
	
	public String getHostname() {
		return hostname;
	}

	protected String getCommandOptions()
	{
		String stringifiedOptions = "";
		
		for (String option: options)
			stringifiedOptions += option + " ";
		
		return stringifiedOptions.trim();
	}
	
	protected abstract T parseElement(String line);
	
}
