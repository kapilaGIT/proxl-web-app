package org.yeastrc.xlink.www.lorikeet_dto;

import java.util.List;

public class LorikeetPerPeptideData {

	private String sequence;
	private List<LorikeetVariableMod> variableMods;
	
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public List<LorikeetVariableMod> getVariableMods() {
		return variableMods;
	}
	public void setVariableMods(List<LorikeetVariableMod> variableMods) {
		this.variableMods = variableMods;
	}
}
