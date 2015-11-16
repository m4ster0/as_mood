package com.pwr.ibi.asmood.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pwr.ibi.asmood.model.DataModel;

public abstract class CSVParser<T extends DataModel> {
	
	protected String filePath;
	protected String delimeter;
	
	protected List<T> results;

	public CSVParser(String filePath, String delimeter)
	{
		this.filePath = filePath;
		this.delimeter = delimeter;
		
		results = new ArrayList<T>();
	}
	
	public void parse()
	{
		BufferedReader bReader = null;
		String currentLine = "";

		try {
			bReader = new BufferedReader(new FileReader(filePath));
			while ((currentLine = bReader.readLine()) != null) {
				String[] lineFields = currentLine.split(delimeter);
				
				T result = parseLine(lineFields);
				if(result != null)
					results.add(result);
			}
			bReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected abstract T parseLine(String[] lineFields);

	public List<T> getResults() {
		return results;
	}
}
