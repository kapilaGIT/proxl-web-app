package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_PsmDAO;
import org.yeastrc.proxl.import_xml_to_db.dao_db_insert.DB_Insert_SearchScanFilenameDAO;
import org.yeastrc.proxl.import_xml_to_db.exceptions.ProxlImporterDataException;
import org.yeastrc.proxl.import_xml_to_db.utils.RoundDecimalFieldsIfNecessary;
import org.yeastrc.proxl_import.api.xml_dto.Psm;
import org.yeastrc.proxl.import_xml_to_db.dto.PsmDTO;
import org.yeastrc.proxl.import_xml_to_db.dto.SearchScanFilenameDTO;
import org.yeastrc.xlink.dto.ReportedPeptideDTO;
import org.yeastrc.xlink.utils.XLinkUtils;

/**
 * Populate and Save PSM record to the DB
 *
 */
public class PopulateAndSavePsmDTO {

	private static final Logger log = Logger.getLogger( PopulateAndSavePsmDTO.class );
	/**
	 * private constructor
	 */
	private PopulateAndSavePsmDTO(){}
	public static PopulateAndSavePsmDTO getInstance() {
		return new PopulateAndSavePsmDTO();
	}
	
	/**
	 * @param searchId
	 * @param searchProgramEntryMap
	 * @param mapOfScanFilenamesMapsOfScanNumbersToScanIds
	 * @param linkTypeNumber
	 * @param reportedPeptideDTO
	 * @param psm
	 * @return populated and saved PsmDTO
	 * 
	 * @throws ProxlImporterDataException
	 * @throws Exception
	 */
	public PsmDTO populateAndSavePSMDTO(
			int searchId,
			Map<String, Map<Integer, Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIds,
			int linkTypeNumber, 
			ReportedPeptideDTO reportedPeptideDTO, 
			Psm psm,
			Map<String, SearchScanFilenameDTO> scanFilenamesOnPSMsKeyedOnScanFilename
			) throws ProxlImporterDataException, Exception {
		
		PsmDTO psmDTO = new PsmDTO();
		psmDTO.setSearchId( searchId );
		psmDTO.setReportedPeptideId( reportedPeptideDTO.getId() );
		if ( psm.getLinkerMass() != null ) {
			BigDecimal linkerMass = RoundDecimalFieldsIfNecessary.roundDecimalFieldsIfNecessary( psm.getLinkerMass() );
			psmDTO.setLinkerMass( linkerMass );
		} else {
			//  linker mass cannot be null for crosslink or looplink
			if ( linkTypeNumber == XLinkUtils.TYPE_CROSSLINK 
					|| linkTypeNumber == XLinkUtils.TYPE_LOOPLINK ) {
				String msg = "Linker mass cannot be null for Crosslink or Looplink.  " 
						+ "  Psm Scanfilename: " + psm.getScanFileName()
						+ ", Psm ScanNumber: " + psm.getScanNumber();
				log.error( msg );
				throw new ProxlImporterDataException( msg );
			}
		}
		if ( psm.getPrecursorCharge() != null ) {
			psmDTO.setCharge( psm.getPrecursorCharge().intValue() );
		}
		if ( psm.getScanNumber() != null ) {
			psmDTO.setScanNumber( psm.getScanNumber().intValue() );
		}
		if ( StringUtils.isNotEmpty( psm.getScanFileName() ) ) {
			SearchScanFilenameDTO searchScanFilenameDTO = scanFilenamesOnPSMsKeyedOnScanFilename.get( psm.getScanFileName() );
			if ( searchScanFilenameDTO == null ) {
				searchScanFilenameDTO = new SearchScanFilenameDTO();
				searchScanFilenameDTO.setSearchId( searchId );
				searchScanFilenameDTO.setFilename( psm.getScanFileName() );
				DB_Insert_SearchScanFilenameDAO.getInstance().saveToDatabase( searchScanFilenameDTO );
				scanFilenamesOnPSMsKeyedOnScanFilename.put( psm.getScanFileName(), searchScanFilenameDTO );
			}
			psmDTO.setSearchScanFilenameId( searchScanFilenameDTO.getId() );
		}
		if ( psm.getScanNumber() != null
				&& mapOfScanFilenamesMapsOfScanNumbersToScanIds != null 
				&& ( ! mapOfScanFilenamesMapsOfScanNumbersToScanIds.isEmpty() ) ) {
			//  Have scan files so map the scan number to the scan id and put the scan id on the psmDTO
			Map<Integer,Integer> mapOfScanNumbersToScanIds = null;
			String scanFilenameFromMapEntry = null;
			if ( mapOfScanFilenamesMapsOfScanNumbersToScanIds.size() == 1 ) {
				//  If only one scan file, just use the map entry
				Map.Entry<String, Map<Integer,Integer>> mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry =
						mapOfScanFilenamesMapsOfScanNumbersToScanIds.entrySet().iterator().next();
				scanFilenameFromMapEntry = mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry.getKey();
				mapOfScanNumbersToScanIds = mapOfScanFilenamesMapsOfScanNumbersToScanIdsEntry.getValue();
			} else {
				//  More than one scan file so get the scan file entry for this PSM
				mapOfScanNumbersToScanIds = mapOfScanFilenamesMapsOfScanNumbersToScanIds.get( psm.getScanFileName() );
				if ( mapOfScanNumbersToScanIds == null ) {
					String msg = "No Scan Numbers to Scan Ids Mapping for Scan File: " + psm.getScanFileName();
					log.error( msg );
					throw new ProxlImporterDataException(msg);
				}
			}
			int scanNumberInPSM = psm.getScanNumber().intValue();
			Integer scanId = mapOfScanNumbersToScanIds.get( scanNumberInPSM );
			if ( scanId == null ) {
				String msg = "No Scan Id Mapping for Scan Number: " + scanNumberInPSM
						+ ", PSM Scan File: " + psm.getScanFileName()
						+ ", Scan Filename From command line: " + scanFilenameFromMapEntry;
				log.error( msg );
				throw new ProxlImporterDataException(msg);
			}
			psmDTO.setScanId( scanId );
		}
		DB_Insert_PsmDAO.getInstance().saveToDatabase( psmDTO );
		return psmDTO;
	}
}