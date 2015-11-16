package com.pwr.ibi.asmood.gui.panels;

import com.pwr.ibi.asmood.ASMoodManager;

public class ASMoodResearchPanel extends ASMoodPanel {
	
	private static final long serialVersionUID = 5158491118692380602L;
	
	public static String TAB_TITLE = "Research";
	
	private ASMoodResearchTablePanel tablePanel;

	public ASMoodResearchPanel(ASMoodManager asMoodManager) {
		super(asMoodManager);
	}

	@Override
	protected void initUI() {
		
		tablePanel = new ASMoodResearchTablePanel(asMoodManager);
		
		add(tablePanel);
	}

}
