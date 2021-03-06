package org.yeastrc.xlink.www.qc_plots.scan_retention_time;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.yeastrc.xlink.dao.ScanRetentionTimeDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.ScanRetentionTimeDTO;
import org.yeastrc.xlink.www.annotation_utils.GetAnnotationTypeData;
import org.yeastrc.xlink.www.exceptions.ProxlWebappDataException;
import org.yeastrc.xlink.www.project_search__search__mapping.MapProjectSearchIdToSearchId;
import org.yeastrc.xlink.www.searcher.RetentionTimesFromScanTblSearcher;
import org.yeastrc.xlink.www.web_utils.RetentionTimeScalingAndRounding;

/**
 * 
 *
 */
public class CreateScanRetentionTimeQCPlotData {

	private static final Logger log = Logger.getLogger(CreateScanRetentionTimeQCPlotData.class);
	
	private static final int BIN_COUNT = 100;  //  Number of bars on the chart
	private static final int EXCLUDE_SCAN_LEVEL_1 = 1;
	
	/**
	 * private constructor
	 */
	private CreateScanRetentionTimeQCPlotData(){}
	public static CreateScanRetentionTimeQCPlotData getInstance( ) throws Exception {
		CreateScanRetentionTimeQCPlotData instance = new CreateScanRetentionTimeQCPlotData();
		return instance;
	}
	
	/**
	 * @param scansForSelectedLinkTypes
	 * @param projectSearchId
	 * @param scanFileId
	 * @param annotationTypeId
	 * @param psmScoreCutoff
	 * @param retentionTimeInSecondsCutoff
	 * @return
	 * @throws Exception
	 */
	public ScanRetentionTimeJSONRoot create( 
			List<String> scansForSelectedLinkTypes,	
			int projectSearchId, 
			int scanFileId, 
			int annotationTypeId,
			double psmScoreCutoff, 
			Double retentionTimeInSecondsCutoff ) throws Exception {
		List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList = null;
		if ( ! scansForSelectedLinkTypes.isEmpty() ) {
			//  Process retention times for psms that meet criteria
			Integer searchId =
					MapProjectSearchIdToSearchId.getInstance().getSearchIdFromProjectSearchId( projectSearchId );
			if ( searchId == null ) {
				String msg = ": No searchId found for projectSearchId: " + projectSearchId;
				log.warn( msg );
			    throw new ProxlWebappDataException( msg );
			}
			List<Integer> searchIds = new ArrayList<>( 1 );
			searchIds.add( searchId );
			Map<Integer, Map<Integer, AnnotationTypeDTO>> annotationTypeDataMappedOnSearchIdAnnTypeId =
					GetAnnotationTypeData.getInstance().getAll_Psm_Filterable_ForSearchIds( searchIds );
			Map<Integer, AnnotationTypeDTO> annotationTypeDataMappedOnAnnTypeId = annotationTypeDataMappedOnSearchIdAnnTypeId.get( searchId );
			if ( annotationTypeDataMappedOnAnnTypeId == null ) {
				String msg = "No PSM Annotation Type data retrieved for search id: " + searchId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			AnnotationTypeDTO annotationTypeDTO = annotationTypeDataMappedOnAnnTypeId.get( annotationTypeId );
			if ( annotationTypeDTO == null ) {
				String msg = "No PSM Annotation Type data retrieved for search id: " + searchId + ", annotationTypeId: " + annotationTypeId;
				log.error( msg );
				throw new ProxlWebappDataException(msg);
			}
			retentionTimeForPSMsthatMeetCriteriaList = 
					RetentionTimesFromScanTblSearcher.getInstance()
					.getRetentionTimes( scansForSelectedLinkTypes, searchId, scanFileId, annotationTypeDTO, psmScoreCutoff, retentionTimeInSecondsCutoff );
		}
		return createScanRetentionTimeQCPlotData( scanFileId, retentionTimeInSecondsCutoff, retentionTimeForPSMsthatMeetCriteriaList );
	}
	
	/**
	 * @param scanFileId
	 * @param retentionTimeInSecondsCutoff
	 * @param retentionTimeForPSMsthatMeetCriteriaList
	 * @return
	 * @throws Exception
	 */
	private ScanRetentionTimeJSONRoot createScanRetentionTimeQCPlotData( 
			int scanFileId, 
			Double retentionTimeInSecondsCutoff, 
			List<BigDecimal> retentionTimeForPSMsthatMeetCriteriaList ) throws Exception {
		int numScans = 0;
		boolean firstOverallRetentionTimeEntry = true;
		List<ScanRetentionTimeDTO> scanRetentionTime_AllPSMsExcludeScanLevel_1_List = 
				ScanRetentionTimeDAO.getForScanFileIdExcludeScanLevel( scanFileId, retentionTimeInSecondsCutoff, EXCLUDE_SCAN_LEVEL_1 );
		//  Find max and min values
		double retentionTimeMin = 0;
		double retentionTimeMax =  0;
		for ( ScanRetentionTimeDTO scanRetentionTimeDTO : scanRetentionTime_AllPSMsExcludeScanLevel_1_List ) {
			BigDecimal retentionTime = scanRetentionTimeDTO.getRetentionTime();
			double retentionTimeScaled = 
					RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime ).doubleValue();
			if ( firstOverallRetentionTimeEntry  ) {
				firstOverallRetentionTimeEntry = false;
				retentionTimeMin = retentionTimeScaled;
				retentionTimeMax = retentionTimeScaled;
			} else {
				if ( retentionTimeScaled < retentionTimeMin ) {
					retentionTimeMin = retentionTimeScaled;
				}
				if ( retentionTimeScaled > retentionTimeMax  ) {
					retentionTimeMax = retentionTimeScaled;
				}
			}
		}
		double retentionTimeMaxMinusMin = retentionTimeMax - retentionTimeMin;
		//  Process data into bins
		double binSizeAsDouble = ( retentionTimeMax ) / BIN_COUNT;
		//   First process the retention times for all PSMs for the scan file into bins, excluding scan level 1 (EXCLUDE_SCAN_LEVEL_1)
		int[] retentionTimeCountsAllPSMs = new int[ BIN_COUNT ];
		for ( ScanRetentionTimeDTO scanRetentionTimeDTO : scanRetentionTime_AllPSMsExcludeScanLevel_1_List ) {
			BigDecimal retentionTime = scanRetentionTimeDTO.getRetentionTime();
			double retentionTimeFraction = RetentionTimeScalingAndRounding.retentionTimeToMinutes( retentionTime ).doubleValue() / retentionTimeMax;
			int bin = (int) ( (  retentionTimeFraction ) * BIN_COUNT );
			if ( bin < 0 ) {
				bin = 0;
			} else if ( bin >= BIN_COUNT ) {
				bin = BIN_COUNT - 1;
			} 
			retentionTimeCountsAllPSMs[ bin ]++;
		}
		//  Second process the retention times for the PSMs that meet the selection criteria for the scan file into bins
		int[] retentionTimeForPsmthatMeetCriteriaCounts = new int[ BIN_COUNT ];
		if ( retentionTimeForPSMsthatMeetCriteriaList != null ) {
			for ( BigDecimal retentionTimeForPSMsthatMeetCriteria : retentionTimeForPSMsthatMeetCriteriaList ) {
				double retentionTimeFraction = 
						RetentionTimeScalingAndRounding.retentionTimeToMinutes(  retentionTimeForPSMsthatMeetCriteria ).doubleValue() / retentionTimeMax;
				int bin = (int) ( (  retentionTimeFraction ) * BIN_COUNT );
				if ( bin < 0 ) {
					bin = 0;
				} else if ( bin >= BIN_COUNT ) {
					bin = BIN_COUNT - 1;
				} 
				retentionTimeForPsmthatMeetCriteriaCounts[ bin ]++;
			}
		}
		//  Populate objects to generate JSON
		ScanRetentionTimeJSONRoot scanRetentionTimeJSONRoot = new ScanRetentionTimeJSONRoot();
		scanRetentionTimeJSONRoot.setScanFileId(scanFileId);
		scanRetentionTimeJSONRoot.setNumScans( numScans );
		scanRetentionTimeJSONRoot.setRetentionTimeMax( retentionTimeMaxMinusMin );
		scanRetentionTimeJSONRoot.setRetentionTimeMin( retentionTimeMin );
		List<ScanRetentionTimeJSONChartBucket> chartBuckets = new ArrayList<>();
		scanRetentionTimeJSONRoot.setChartBuckets( chartBuckets );
		double binHalf = binSizeAsDouble / 2 ;
		//  Take the data in the bins and  create "buckets" in the format required for the charting API
		for ( int binIndex = 0; binIndex < retentionTimeCountsAllPSMs.length; binIndex++ ) {
			ScanRetentionTimeJSONChartBucket chartBucket = new ScanRetentionTimeJSONChartBucket();
			chartBuckets.add( chartBucket );
			int retentionTimeCount = retentionTimeCountsAllPSMs[ binIndex ];
			int retentionTimeForPsmsThatMeetCriteriaCount = retentionTimeForPsmthatMeetCriteriaCounts[ binIndex ];
			double binStartDouble = ( ( binIndex * binSizeAsDouble ) );
			if ( binIndex == 0 && binStartDouble < 0.1 ) {
				chartBucket.setBinStart( 0 );
			} else { 
				int binStart = (int)Math.round( binStartDouble );
				chartBucket.setBinStart( binStart );
			}
			int binEnd = (int)Math.round( ( binIndex + 1 ) * binSizeAsDouble );
			chartBucket.setBinEnd( binEnd );
			double binMiddleDouble = binStartDouble + binHalf;
			chartBucket.setBinCenter( binMiddleDouble );
			chartBucket.setTotalCount( retentionTimeCount );
			chartBucket.setCountForPsmsThatMeetCriteria( retentionTimeForPsmsThatMeetCriteriaCount );
		}
		return scanRetentionTimeJSONRoot;
	}
}
