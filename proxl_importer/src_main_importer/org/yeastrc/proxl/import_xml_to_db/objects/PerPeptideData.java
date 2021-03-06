package org.yeastrc.proxl.import_xml_to_db.objects;

import java.util.List;

import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;


/**
 * Importer Internal
 * 
 * Data from processing Peptide data.
 *
 * All of these entries will be updated with the IDs in other objects before they are saved 
 * on a per Reported Peptide basis
 * 
 */
public class PerPeptideData {
	
	private PeptideDTO peptideDTO;

	private SrchRepPeptPeptideDTO srchRepPeptPeptideDTO;
		
	private List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Peptide;
	
	private List<Integer> monolinkPositionList;
	
	private List<MonolinkContainer> monolinkContainerList;
	
	/**
	 * Only maps to 1 protein
	 */
	private boolean peptideIdMapsToOnlyOneProtein;
	

	/**
	 * Only maps to 1 protein
	 * @return
	 */
	public boolean isPeptideIdMapsToOnlyOneProtein() {
		return peptideIdMapsToOnlyOneProtein;
	}

	/**
	 * Only maps to 1 protein
	 * 
	 * @param peptideIdMapsToOnlyOneProtein
	 */
	public void setPeptideIdMapsToOnlyOneProtein(boolean peptideIdMapsToOnlyOneProtein) {
		this.peptideIdMapsToOnlyOneProtein = peptideIdMapsToOnlyOneProtein;
	}


	public PeptideDTO getPeptideDTO() {
		return peptideDTO;
	}

	public void setPeptideDTO(PeptideDTO peptideDTO) {
		this.peptideDTO = peptideDTO;
	}

	public List<SrchRepPeptPeptDynamicModDTO> getSrchRepPeptPeptDynamicModDTOList_Peptide() {
		return srchRepPeptPeptDynamicModDTOList_Peptide;
	}

	public void setSrchRepPeptPeptDynamicModDTOList_Peptide(
			List<SrchRepPeptPeptDynamicModDTO> srchRepPeptPeptDynamicModDTOList_Peptide) {
		this.srchRepPeptPeptDynamicModDTOList_Peptide = srchRepPeptPeptDynamicModDTOList_Peptide;
	}

	public List<Integer> getMonolinkPositionList() {
		return monolinkPositionList;
	}

	public void setMonolinkPositionList(List<Integer> monolinkPositionList) {
		this.monolinkPositionList = monolinkPositionList;
	}

	public SrchRepPeptPeptideDTO getSrchRepPeptPeptideDTO() {
		return srchRepPeptPeptideDTO;
	}

	public void setSrchRepPeptPeptideDTO(SrchRepPeptPeptideDTO srchRepPeptPeptideDTO) {
		this.srchRepPeptPeptideDTO = srchRepPeptPeptideDTO;
	}

	public List<MonolinkContainer> getMonolinkContainerList() {
		return monolinkContainerList;
	}

	public void setMonolinkContainerList(
			List<MonolinkContainer> monolinkContainerList) {
		this.monolinkContainerList = monolinkContainerList;
	}
	
	
}