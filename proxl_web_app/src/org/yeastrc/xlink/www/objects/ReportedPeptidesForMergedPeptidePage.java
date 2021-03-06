package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * 
 *
 */
public class ReportedPeptidesForMergedPeptidePage implements SearchPeptideCommonLinkWebserviceResultIF {

	private ReportedPeptideDTO reportedPeptide;
	
	private String searchName;
	
	private int searchId;

	
	private int numPSMs;
	
	private List<String> peptideAnnotationValues;
	
	private List<String> psmAnnotationValues;
	

	@Override
	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValues;
	}
	@Override
	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValues = psmAnnotationValueList;
	}
	@Override
	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValues;
	}
	@Override
	public void setPeptideAnnotationValueList(List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValues = peptideAnnotationValueList;
	}
	
	public List<String> getPeptideAnnotationValues() {
		return peptideAnnotationValues;
	}
	public void setPeptideAnnotationValues(List<String> peptideAnnotationValues) {
		this.peptideAnnotationValues = peptideAnnotationValues;
	}
	public List<String> getPsmAnnotationValues() {
		return psmAnnotationValues;
	}
	public void setPsmAnnotationValues(List<String> psmAnnotationValues) {
		this.psmAnnotationValues = psmAnnotationValues;
	}
	public String getSearchName() {
		return searchName;
	}
	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}
	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}
	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}
	public int getSearchId() {
		return searchId;
	}
	public void setSearchId(int searchId) {
		this.searchId = searchId;
	}

	public int getNumPSMs() {
		return numPSMs;
	}
	public void setNumPSMs(int numPSMs) {
		this.numPSMs = numPSMs;
	}

	
	
}
