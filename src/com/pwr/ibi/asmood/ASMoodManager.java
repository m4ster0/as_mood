package com.pwr.ibi.asmood;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.pwr.ibi.asmood.gui.panels.ASMoodManagerListener;
import com.pwr.ibi.asmood.gui.panels.ASMoodResearchPanel.ASMoodProcessState;
import com.pwr.ibi.asmood.gui.panels.ASMoodResearchStateListener;
import com.pwr.ibi.asmood.io.ASDataCSVParser;
import com.pwr.ibi.asmood.io.CSVWriter;
import com.pwr.ibi.asmood.model.ASModel;

public class ASMoodManager implements ASMoodExplorationListener {
	
	private List<ASModel> asModels;
	private List<ASMood> asMoods;
	private List<ASMood> workingMoods;
	private List<ASMood> queueMoods;
	private List<String> selectedResearch;
	private boolean initied;
	private int hostMaxCount;
	
	public boolean hostSearchProcessing;
	public boolean exploreProcessing;
	
	private ASMoodManagerListener listener;
	private ASMoodResearchStateListener stateListener;
	
	public ASMoodManager()
	{
		asModels = new ArrayList<ASModel>();
		asMoods = new ArrayList<ASMood>();
		
		workingMoods = new ArrayList<ASMood>();
		queueMoods = new ArrayList<ASMood>();
		selectedResearch = new ArrayList<String>();
		
		initied = false;
		hostSearchProcessing = false;
	}
	
	public void addListener(ASMoodManagerListener listener) {
		this.listener = listener;
	}
	
	public void removeListener(ASMoodManagerListener listener) {
		this.listener = null;
	}
	
	public void addStateListener(ASMoodResearchStateListener listener) {
		this.stateListener = listener;
	}
	
	public void removeStateListener(ASMoodResearchStateListener listener) {
		this.stateListener = null;
	}
	
	public void init(String filePath)
	{
		ASDataCSVParser parser = new ASDataCSVParser(filePath);
		
		parser.parse();
		asModels.addAll(parser.getResults());
		
		initied = true;
		
		if(listener != null)
			listener.notifyManagerInitied(asModels);
	}
	
	public void clear() {
		asModels.clear();
		listener.notifyManagerClear();
	}
	
	private ASModel getASModel(String asn)
	{
		for(ASModel model: asModels)
			if(model.getASN().equals(asn))
				return model;
		
		return null;
	}
	
	public void exploreAS(List<String> researches)
	{
		if(asMoods.isEmpty())
			return;
		
		selectedResearch.clear();
		if(researches.contains(ASMood.PING_TIME_ID) || researches.contains(ASMood.PING_LOST_PACKETS_ID))
			selectedResearch.add(ASMood.PING_ID);
		if(researches.contains(ASMood.TRACEROUTE_HOP_COUNT_ID) || researches.contains(ASMood.TRACEROUTE_TIME_ID))
			selectedResearch.add(ASMood.TRACEROUTE_ID);

		for (ASMood mood: asMoods) {
			if(mood.getAviableHosts().isEmpty())
				continue;
			
			queueMoods.add(mood);
		}
		
		String currentTask = selectedResearch.get(0);
		addWorkingASMoods(2, currentTask);
		
		stateListener.notifyProcessStateChange(ASMoodProcessState.ExploreProcess);
	}

	public void initASMood(String asn) {
		ASModel asModel = getASModel(asn);
		initASMood(asModel);
	}
	
	public void initASMood(ASModel asModel) {
		ASMood asMood = new ASMood(asModel);
		
		asMoods.add(asMood);
	}
	
	public void saveActiveASData() {
		if(asMoods.isEmpty())
			return;
		
		CSVWriter.writeActiveASMood(asMoods);
	}
	
	public void addASMood(ASModel model) {
		initASMood(model);
	}
	
	public void removeASMood(ASModel model) {
		String asMoodASN = null;
		String modelASN = null;
		
		for(Iterator<ASMood> it = asMoods.iterator(); it.hasNext(); ) {
			ASMood asMood = it.next();
			
			asMoodASN = asMood.getASModel().getASN();
			modelASN = model.getASN();
			if(asMoodASN.equals(modelASN))
				it.remove();
		}
	}
	
	public void terminateActive() {
		for(ASMood asMood: workingMoods) {
			asMood.terminateActiveTasks();
			asMood.removeListener(this);
		}
		workingMoods.clear();
		queueMoods.clear();
		
		stateListener.notifyProcessStateChange(ASMoodProcessState.IdleProcess);
		hostSearchProcessing = false;
	}
	
	public void searchForHosts(int maxCount)
	{
		if(asMoods.isEmpty())
			return;
		
		hostMaxCount = maxCount;
		for (ASMood mood: asMoods)
		{
			if(mood.getAviableHosts().size() >= maxCount)
				continue;
			
			queueMoods.add(mood);
		}
		
		addWorkingASMoods(2, ASMood.HOST_SEARCH_ID);
		
		stateListener.notifyProcessStateChange(ASMoodProcessState.HostSearchProcess);
		hostSearchProcessing = true;
	}

	@Override
	public void notifySearchHostsCompleted(ASMood asMood) {
		workingMoods.remove(asMood);
		asMood.removeListener(this);
		addWorkingASMoods(1, ASMood.HOST_SEARCH_ID);
		
		float progress = (float) (asMoods.size() - (workingMoods.size() + queueMoods.size())) / asMoods.size();
		listener.notifyHostSearchProgress(progress, asMood.getASModel(), asMood.getAviableHosts());
		if(workingMoods.isEmpty() && queueMoods.isEmpty()) {
			stateListener.notifyProcessStateChange(ASMoodProcessState.IdleProcess);
			hostSearchProcessing = false;
		}
		
	}
	
	private void addWorkingASMoods(int count, String taskId) {
		int moodCounter = 0;
		
		List<ASMood> toRemove = new ArrayList<ASMood>();
		
		for(ASMood asMood: queueMoods) {
			workingMoods.add(asMood);
			toRemove.add(asMood);
			asMood.addListener(this);
			
			boolean taskAdded = addASMoodTask(asMood, taskId);
			if(taskAdded)
				moodCounter++;
			
			if(moodCounter >= count)
				break;
		}
		
		for(ASMood mood: toRemove)
			queueMoods.remove(mood);
	}
	
	private boolean addASMoodTask(ASMood asMood, String taskId) {
		boolean taskAdded = false;
		
		switch(taskId) {
			case ASMood.HOST_SEARCH_ID:
				asMood.searchAviableHosts(hostMaxCount);
				taskAdded = true;
				break;
			case ASMood.PING_ID:
				asMood.exploreASPing();
				taskAdded = true;
				break;
			case ASMood.TRACEROUTE_ID:
				asMood.exploreASTraceroute();
				taskAdded = true;
				break;
		}
		
		return taskAdded;
	}

	@Override
	public void notifyASExplorationCompleted(String taskId, ASMood asMood) {
		workingMoods.remove(asMood);
		asMood.removeListener(this);
		
		addWorkingASMoods(1, selectedResearch.get(0));
		
		if(workingMoods.isEmpty() && queueMoods.isEmpty()) {
			System.out.println("ASMoods research finished for taskId: " + selectedResearch.get(0));
			selectedResearch.remove(0);
			
			if(selectedResearch.isEmpty()) {
				System.out.println("Writing results started...");
				Date now = new Date();
				for(ASMood mood: asMoods) {
					if(mood.getAviableHosts().isEmpty())
						continue;
					
					System.out.println("Writing results for " + mood.getASModel().getASN() + "...");
					CSVWriter.writeResults(mood, now);
				}
				System.out.println("Writing results completed");
				stateListener.notifyProcessStateChange(ASMoodProcessState.IdleProcess);
			}
			else {
				for (ASMood mood: asMoods)
					queueMoods.add(mood);
				
				String currentTask = selectedResearch.get(0);
				addWorkingASMoods(2, currentTask);
			}
		}
	}
	
	
}
