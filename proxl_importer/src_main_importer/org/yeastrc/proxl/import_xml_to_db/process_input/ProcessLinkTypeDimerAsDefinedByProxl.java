package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao.ProteinImporterContainerDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchReportedPeptideDynamicModLookupDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SrchRepPeptProtSeqIdPosDimerDAO;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideProteinPositionDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.ProteinSequenceDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchReportedPeptideDynamicModLookupDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosMonolinkDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptProtSeqIdPosDimerDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptDynamicModDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SrchRepPeptPeptideDTO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterInteralException;
import org.yeastrc.proxl.import_xml_to_db.objects.MonolinkContainer;
import org.yeastrc.proxl.import_xml_to_db.objects.PerPeptideData;
import org.yeastrc.proxl.import_xml_to_db.objects.ProteinImporterContainer;
import org.yeastrc.proxl.import_xml_to_db.peptide_protein_position.PeptideProteinPositionDTO_SaveToDB_NoDups;
import org.yeastrc.proxl_import.api.xml_dto.Peptide;
import org.yeastrc.proxl_import.api.xml_dto.Peptides;
import org.yeastrc.proxl_import.api.xml_dto.ReportedPeptide;
import org.yeastrc.proxl.import_xml_to_db.dto.PeptideDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.linkable_positions.linkers.ILinker;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * Proxl internal "Dimer" is two peptides that are not a crosslink (not linked to each other)
 *
 */
public class ProcessLinkTypeDimerAsDefinedByProxl {
	
	private static final Logger log = Logger.getLogger(ProcessLinkTypeDimerAsDefinedByProxl.class);
	//  private constructor
	private ProcessLinkTypeDimerAsDefinedByProxl() { }
	public static ProcessLinkTypeDimerAsDefinedByProxl getInstance() { return new ProcessLinkTypeDimerAsDefinedByProxl(); }
	/**
	 * result from GetDimerProteinMappingsSinglePeptideData method
	 *
	 */
	public static class GetDimerProteinMappingsResult {
		private boolean noProteinMappings;
		private List<GetDimerProteinMappingsSinglePeptideData> getDimerroteinMappingsSinglePeptideDataList = new ArrayList<>();
		/** 
		 * No Protein mappings (Either peptide has No Protein mappings) 
		 * @return
		 */
		public boolean isNoProteinMappings() {
			return noProteinMappings;
		}
		public void setNoProteinMappings(boolean noProteinMappings) {
			this.noProteinMappings = noProteinMappings;
		}
	}
	/**
	 * Data for each peptide in the dimer
	 *
	 */
	private static class GetDimerProteinMappingsSinglePeptideData {
		private boolean noProteinMappings;
		private List<SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide;
		private PerPeptideData perPeptideData;
	}
	private static class SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair {
		ProteinImporterContainer proteinImporterContainer;
		SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO;
		Collection<Integer> peptidePositionsInProteinCollection;
	}
	
	/**
	 * Get Protein Mappings for dimer reported peptide
	 * 
	 * The PeptideDTO entries are saved to the DB in this step since used for Protein Mappings
	 * 
	 * @param reportedPeptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @return
	 * @throws Exception
	 */
	public GetDimerProteinMappingsResult getDimerProteinMappings( 
			ReportedPeptide reportedPeptide, 
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs
			) throws Exception {
		
		GetDimerProteinMappingsResult getDimerMappingsResult = new GetDimerProteinMappingsResult();
		Peptides peptides =
				reportedPeptide.getPeptides();
		if ( peptides == null ) {
			String msg = "There must be 2 peptides for Dimer reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		List<Peptide> peptideList = peptides.getPeptide();
		if ( peptideList == null || peptideList.size() != 2 ) {
			String msg = "There must be 2 peptides for Dimer for reported peptide: " + reportedPeptide.getReportedPeptideString();
			log.error( msg );
			throw new ProxlImporterDataException(msg);
		}
		int peptideNumberInt = 0;
		for ( Peptide peptide : peptideList ) {
			peptideNumberInt++;
			GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData =
					getProteinMappingForSinglePeptide( 
							peptide, 
							linkerList, 
							linkerListStringForErrorMsgs, 
							reportedPeptide, 
							Integer.toString( peptideNumberInt ) );
			getDimerMappingsResult.getDimerroteinMappingsSinglePeptideDataList.add( getDimerroteinMappingsSinglePeptideData );
			if ( getDimerroteinMappingsSinglePeptideData.noProteinMappings ) {
				getDimerMappingsResult.noProteinMappings = true;
			}
		}
		return getDimerMappingsResult;
	}
	
	/**
	 * @param peptide
	 * @param linkerList
	 * @param linkerListStringForErrorMsgs
	 * @param reportedPeptide
	 * @param peptideNumber
	 * @return
	 * @throws Exception
	 */
	private GetDimerProteinMappingsSinglePeptideData getProteinMappingForSinglePeptide( 
			Peptide peptide, 
			List<ILinker> linkerList,
			String linkerListStringForErrorMsgs,
			ReportedPeptide reportedPeptide,
			String peptideNumber
			) throws Exception {
		
		GetDimerProteinMappingsSinglePeptideData getDimerProteinMappingsSinglePeptideData = new GetDimerProteinMappingsSinglePeptideData();
		PerPeptideData perPeptideData = GetPerPeptideData.getInstance().getPerPeptideData( peptide );
		getDimerProteinMappingsSinglePeptideData.perPeptideData = perPeptideData;
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		Set<Integer> peptideMonolinkPositions = 
				GetPeptideMonolinkPositions.getInstance().getPeptideMonolinkPositions( peptide );
		//  Create partial SrchRepPeptPeptideDTO peptide level record
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = new SrchRepPeptPeptideDTO();
		srchRepPeptPeptideDTO.setPeptideId( peptideDTO.getId() );
		perPeptideData.setSrchRepPeptPeptideDTO( srchRepPeptPeptideDTO );
		Collection<ProteinImporterContainer> proteinMatches_Peptide = 
				GetProteinsForPeptide.getInstance()
				.getProteinsForPeptides( peptideDTO.getSequence() );
		// get proteins and linkable positions in those proteins that are mapped to by the given peptides and positions
		Map<ProteinImporterContainer, Collection<Integer>> proteinMap = 
				GetLinkableProteinsAndPositions.getInstance()
				.get_Unlinked_Dimer_PeptidePositionsInProteins( 
						reportedPeptide, 
						peptide, 
						peptideDTO.getSequence(), 
						peptideMonolinkPositions, 
						linkerList, 
						proteinMatches_Peptide );
		if( proteinMap.size() < 1 ) {
			String msg = null;
			if ( peptideMonolinkPositions != null && ( ! peptideMonolinkPositions.isEmpty() ) ) {
				msg = "Could not map this peptide and monolink positions to any protein in the Proxl XML file for peptide " 
					+ peptide.getSequence()
					+ ", monolink position(s): " + StringUtils.join( peptideMonolinkPositions, ", " )
					+ " for "
					 + " linker(s).  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			} else {
				msg = "Could not map this peptide to any protein in the Proxl XML file for " 
					+ peptide.getSequence()
					 + ".  reportedPeptide sequence: " + reportedPeptide.getReportedPeptideString();
			}
			log.error( "getProteinMappingForSinglePeptide(...): " + msg );
			throw new ProxlImporterDataException( msg );
		}
		///  Data in perPeptideData for Monolinks
		List<MonolinkContainer> monolinkContainerList = new ArrayList<>();
		perPeptideData.setMonolinkContainerList( monolinkContainerList );
		List<Integer> peptideMonolinkPositionList = perPeptideData.getMonolinkPositionList();
		List <SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList = new ArrayList<>();
		for( Map.Entry<ProteinImporterContainer, Collection<Integer>> proteinMapEntry : proteinMap.entrySet() ) {
			ProteinImporterContainer proteinImporterContainer = proteinMapEntry.getKey();
			Collection<Integer> peptidePositionsInProteinCollection = proteinMapEntry.getValue();
			SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO = new SrchRepPeptProtSeqIdPosDimerDTO();
			SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair =
					new SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair();
			srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer = proteinImporterContainer;
			srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosDimerDTO = srchRepPeptProtSeqIdPosDimerDTO;
			srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.peptidePositionsInProteinCollection = peptidePositionsInProteinCollection;
			srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList.add( srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair );
			for ( Integer peptidePositionsInProtein : peptidePositionsInProteinCollection ) {
				//  Process the monolink positions
				for ( Integer peptideMonolinkPosition : peptideMonolinkPositionList ) {
					//  Convert peptide monolink position to protein position
					int proteinMonolinkPosition = peptidePositionsInProtein + peptideMonolinkPosition - 1; 
					SrchRepPeptProtSeqIdPosMonolinkDTO srchRepPeptProtSeqIdPosMonolinkDTO = new SrchRepPeptProtSeqIdPosMonolinkDTO();
					srchRepPeptProtSeqIdPosMonolinkDTO.setPeptidePosition( peptideMonolinkPosition );
					srchRepPeptProtSeqIdPosMonolinkDTO.setProteinSequencePosition( proteinMonolinkPosition );
					MonolinkContainer monolinkContainer = new MonolinkContainer();
					monolinkContainer.setProteinImporterContainer( proteinImporterContainer );
					monolinkContainer.setSrchRepPeptProtSeqIdPosMonolinkDTO( srchRepPeptProtSeqIdPosMonolinkDTO );
					monolinkContainerList.add( monolinkContainer );
				}
			}
		}  //end looping over proteins
		if ( srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList == null || srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList.isEmpty() ) {
			getDimerProteinMappingsSinglePeptideData.noProteinMappings = true; 
		}
		getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide =
				srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList;
		return getDimerProteinMappingsSinglePeptideData;
	}
	
	/**
	 * Save dimer data to DB
	 * 
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getDimerMappingsResult
	 * @throws Exception
	 */
	public  List<PerPeptideData> saveDimerData( 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			GetDimerProteinMappingsResult getDimerMappingsResult,
			Set<Double> uniqueDynamicModMassesForTheSearch
			) throws Exception {
		
		List<PerPeptideData> perPeptideDataList = new ArrayList<>( 2 );
		for ( GetDimerProteinMappingsSinglePeptideData getDimerroteinMappingsSinglePeptideData : 
			getDimerMappingsResult.getDimerroteinMappingsSinglePeptideDataList ) {
			saveDimerDataSinglePeptide( reportedPeptideDTO, searchId, getDimerroteinMappingsSinglePeptideData, uniqueDynamicModMassesForTheSearch );
			perPeptideDataList.add( getDimerroteinMappingsSinglePeptideData.perPeptideData );
		}
		return perPeptideDataList;
	}
	
	/**
	 * @param reportedPeptideDTO
	 * @param searchId
	 * @param getDimerProteinMappingsSinglePeptideData
	 * @throws Exception
	 */
	private void saveDimerDataSinglePeptide( 
			ReportedPeptideDTO reportedPeptideDTO, 
			int searchId, 
			GetDimerProteinMappingsSinglePeptideData getDimerProteinMappingsSinglePeptideData,
			Set<Double> uniqueDynamicModMassesForTheSearch
			) throws Exception {
		
		//  Save ProteinImporterContainer if needed first since used in SavePerPeptideData.getInstance().savePerPeptideData(...)
		for ( SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair 
				: getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			proteinImporterContainer.setSearchId( searchId );
			ProteinImporterContainerDAO.getInstance().saveProteinImporterContainerIfNeeded( proteinImporterContainer );
		}
		PerPeptideData perPeptideData = getDimerProteinMappingsSinglePeptideData.perPeptideData;
		//  Save SrchRepPeptPeptideDTO, SrchRepPeptPeptDynamicModDTO, SrchRepPeptProtSeqIdPosMonolinkDTO
		SavePerPeptideData.getInstance().savePerPeptideData( perPeptideData, searchId, reportedPeptideDTO );
		//  srchRepPeptPeptideDTO saved in savePerPeptideData(...)
		SrchRepPeptPeptideDTO srchRepPeptPeptideDTO = perPeptideData.getSrchRepPeptPeptideDTO();
		int searchReportedPeptidepeptideId = srchRepPeptPeptideDTO.getId();
		PeptideDTO peptideDTO = perPeptideData.getPeptideDTO();
		int peptideLength = peptideDTO.getSequence().length();
		//  Save Dimer Protein Mappings 
		for ( SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair 
				: getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide ) {
			ProteinImporterContainer proteinImporterContainer = srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.proteinImporterContainer;
			ProteinSequenceDTO proteinSequenceDTO = proteinImporterContainer.getProteinSequenceDTO();
			SrchRepPeptProtSeqIdPosDimerDTO srchRepPeptProtSeqIdPosDimerDTO = srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosDimerDTO;
			srchRepPeptProtSeqIdPosDimerDTO.setProteinSequenceId( proteinSequenceDTO.getId() );
			srchRepPeptProtSeqIdPosDimerDTO.setSearchId( searchId );
			srchRepPeptProtSeqIdPosDimerDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
			srchRepPeptProtSeqIdPosDimerDTO.setSearchReportedPeptidepeptideId( searchReportedPeptidepeptideId );
			DB_Insert_SrchRepPeptProtSeqIdPosDimerDAO.getInstance().save( srchRepPeptProtSeqIdPosDimerDTO );
			//  Insert PeptideProteinPositionDTO record for protein coverage
			Collection<Integer> peptidePositionsInProteinCollection = srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.peptidePositionsInProteinCollection;
			for ( Integer peptidePositionInProtein : peptidePositionsInProteinCollection ) {
				PeptideProteinPositionDTO peptideProteinPositionDTO = new PeptideProteinPositionDTO();
				peptideProteinPositionDTO.setSearchId( searchId );
				peptideProteinPositionDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
				peptideProteinPositionDTO.setPeptideId( peptideDTO.getId() );
				peptideProteinPositionDTO.setProteinSequenceId( proteinSequenceDTO.getId() );
				peptideProteinPositionDTO.setProteinStartPosition( peptidePositionInProtein );
				peptideProteinPositionDTO.setProteinEndPosition( peptidePositionInProtein + peptideLength - 1 );
				PeptideProteinPositionDTO_SaveToDB_NoDups.getInstance().peptideProteinPositionDTO_SaveToDB_NoDups( peptideProteinPositionDTO );
			}
		}
		//  Save Dynamic Mod Masses into Lookup table and into Set for Search level lookup
		for ( SrchRepPeptPeptDynamicModDTO srchRepPeptPeptDynamicModDTO : perPeptideData.getSrchRepPeptPeptDynamicModDTOList_Peptide() ) {
			SearchReportedPeptideDynamicModLookupDTO item = new SearchReportedPeptideDynamicModLookupDTO();
			item.setDynamicModMass( srchRepPeptPeptDynamicModDTO.getMass() );
			item.setLinkType( XLinkUtils.TYPE_DIMER );
			item.setReportedPeptideId( reportedPeptideDTO.getId() );
			item.setSearchId( searchId );
			DB_Insert_SearchReportedPeptideDynamicModLookupDAO.getInstance().saveToDatabaseIgnoreDuplicates( item );
			//  Accumulate mod mass values across the search 
			uniqueDynamicModMassesForTheSearch.add( srchRepPeptPeptDynamicModDTO.getMass() );
		}
		//   Determine if peptide is only mapped to one protein and save that to perPeptideData
		List<SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair> srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide =
				getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide;	
		if ( srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide == null 
				|| srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide.isEmpty() ) {
			String msg = "ERROR: srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide is null or is empty.";
			log.error( msg );
			throw new ProxlImporterInteralException(msg);
		}
		if ( srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide.size() == 1 ) {
			//  Only one mapped protein record so peptide is unique
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		} else {
			//  More than one mapped protein record so they just have to all have the same protein sequence id
			//  in order for the peptide to be unique
			boolean peptideIdMapsToOnlyOneProtein = true;
			SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair firstSrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair =
					srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide.get( 0 );
			SrchRepPeptProtSeqIdPosDimerDTO firstSrchRepPeptProtSeqIdPosDimerDTO = firstSrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair.srchRepPeptProtSeqIdPosDimerDTO;
			int firstProteinSequenceId = firstSrchRepPeptProtSeqIdPosDimerDTO.getProteinSequenceId();
			for ( SrchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_Pair item : srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide ) {
				if ( firstProteinSequenceId != item.srchRepPeptProtSeqIdPosDimerDTO.getProteinSequenceId() ) {
					//  Found record with different protein sequence id so peptide is not unique
					peptideIdMapsToOnlyOneProtein = false;
					break;
				}
			}
			if ( peptideIdMapsToOnlyOneProtein ) {
				//  Peptide only maps to 1 protein so set petpideUnique to true
				perPeptideData.setPeptideIdMapsToOnlyOneProtein( peptideIdMapsToOnlyOneProtein );
			}
		}
		if ( getDimerProteinMappingsSinglePeptideData.srchRepPeptProtSeqIdPosDimerDTO_ProteinImporterContainer_PairList_Peptide.size() 
				==  1 ) {
			//  Peptide only maps to 1 protein so set petpideUnique to true
			perPeptideData.setPeptideIdMapsToOnlyOneProtein( true );
		}
	}
}
