package org.yeastrc.xlink.www.objects;

import java.util.List;

import org.apache.log4j.Logger;
import org.yeastrc.xlink.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;

/**
 * Result returned from web service
 *
 */
public class SearchPeptideMonolinkWebserviceResult {

	private static final Logger log = Logger.getLogger(SearchPeptideMonolinkWebserviceResult.class);
	
	//  Constructors
	
	public SearchPeptideMonolinkWebserviceResult() {}

	public SearchPeptideMonolinkWebserviceResult( SearchPeptideMonolink searchPeptideMonolink ) {
		
		try {

			this.reportedPeptide = searchPeptideMonolink.getReportedPeptide();
			this.peptide = searchPeptideMonolink.getPeptide();
			this.peptidePosition = searchPeptideMonolink.getPeptidePosition();
						
			this.numPsms = searchPeptideMonolink.getNumPsms();
			this.numUniquePsms = searchPeptideMonolink.getNumUniquePsms();
			
		} catch ( Exception e ) {
			
			String msg = "Failed to populate SearchPeptideMonolinkWebserviceResult from SearchPeptideMonolink";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	private ReportedPeptideDTO reportedPeptide;
	private PeptideDTO peptide;
	private PeptideDTO peptide2;
	private int peptidePosition = -1;
	private int peptidePosition2 = -1;
	
	
	private int numPsms;


	private Integer numUniquePsms;

	/**
	 * Used for display on web page
	 */
	private List<String> psmAnnotationValueList;
	
	

	/**
	 * Used for display on web page
	 */
	private List<String> peptideAnnotationValueList;

	public ReportedPeptideDTO getReportedPeptide() {
		return reportedPeptide;
	}

	public void setReportedPeptide(ReportedPeptideDTO reportedPeptide) {
		this.reportedPeptide = reportedPeptide;
	}

	public PeptideDTO getPeptide() {
		return peptide;
	}

	public void setPeptide(PeptideDTO peptide) {
		this.peptide = peptide;
	}

	public PeptideDTO getPeptide2() {
		return peptide2;
	}

	public void setPeptide2(PeptideDTO peptide2) {
		this.peptide2 = peptide2;
	}

	public int getPeptidePosition1() {
		return peptidePosition;
	}

	public void setPeptidePosition1(int peptidePosition1) {
		this.peptidePosition = peptidePosition1;
	}

	public int getPeptidePosition2() {
		return peptidePosition2;
	}

	public void setPeptidePosition2(int peptidePosition2) {
		this.peptidePosition2 = peptidePosition2;
	}

	public int getNumPsms() {
		return numPsms;
	}

	public void setNumPsms(int numPsms) {
		this.numPsms = numPsms;
	}

	public Integer getNumUniquePsms() {
		return numUniquePsms;
	}

	public void setNumUniquePsms(Integer numUniquePsms) {
		this.numUniquePsms = numUniquePsms;
	}

	public List<String> getPsmAnnotationValueList() {
		return psmAnnotationValueList;
	}

	public void setPsmAnnotationValueList(List<String> psmAnnotationValueList) {
		this.psmAnnotationValueList = psmAnnotationValueList;
	}

	public List<String> getPeptideAnnotationValueList() {
		return peptideAnnotationValueList;
	}

	public void setPeptideAnnotationValueList(
			List<String> peptideAnnotationValueList) {
		this.peptideAnnotationValueList = peptideAnnotationValueList;
	}
	
	
	
	
}