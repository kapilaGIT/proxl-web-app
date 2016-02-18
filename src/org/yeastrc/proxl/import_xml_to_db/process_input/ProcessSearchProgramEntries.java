package org.yeastrc.proxl.import_xml_to_db.process_input;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.yeastrc.proxl.import_xml_to_db.objects.SearchProgramEntry;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.DescriptivePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePeptideAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationType;
import org.yeastrc.proxl_import.api.xml_dto.FilterablePsmAnnotationTypes;
import org.yeastrc.proxl_import.api.xml_dto.ProxlInput;
import org.yeastrc.proxl_import.api.xml_dto.SearchAnnotation;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgram;
import org.yeastrc.proxl_import.api.xml_dto.SearchProgramInfo;
import org.yeastrc.proxl_import.api.xml_dto.SearchPrograms;
import org.yeastrc.xlink.dao.AnnotationTypeDAO;
import org.yeastrc.xlink.dao.SearchProgramsPerSearchDAO;
import org.yeastrc.xlink.dto.AnnotationTypeDTO;
import org.yeastrc.xlink.dto.AnnotationTypeFilterableDTO;
import org.yeastrc.xlink.dto.SearchProgramsPerSearchDTO;
import org.yeastrc.xlink.enum_classes.FilterDirectionType;
import org.yeastrc.xlink.enum_classes.FilterableDescriptiveAnnotationType;
import org.yeastrc.xlink.enum_classes.PsmPeptideAnnotationType;



/**
 * Process <search_programs> entries and their children
 * Insert them into the database and return a Map of Maps to go from names to IDs
 *
 */
public class ProcessSearchProgramEntries {

	private static final Logger log = Logger.getLogger( ProcessSearchProgramEntries.class );
	


	/**
	 * private constructor
	 */
	private ProcessSearchProgramEntries(){}
	
	public static ProcessSearchProgramEntries getInstance() {
		
		return new ProcessSearchProgramEntries();
	}
	
	
	/**
	 * @param proxlInput
	 * @param searchId
	 * @return
	 * @throws Exception
	 */
	public Map<String, SearchProgramEntry> processSearchProgramEntries( ProxlInput proxlInput, int searchId ) throws Exception {
		
		
		Map<String, SearchProgramEntry> searchProgramEntryMap = new HashMap<>();
		
		
		SearchProgramInfo searchProgramInfo = proxlInput.getSearchProgramInfo(); 
		

		
		SearchPrograms proxlInputSearchPrograms = searchProgramInfo.getSearchPrograms();
		
		List<SearchProgram> searchProgramList =
				proxlInputSearchPrograms.getSearchProgram();
		
		for ( SearchProgram searchProgram : searchProgramList ) {
			
			searchProgram.getName();
			
			SearchProgramsPerSearchDTO searchProgramsPerSearchDTO = new SearchProgramsPerSearchDTO();
			
			searchProgramsPerSearchDTO.setSearchId( searchId );
			searchProgramsPerSearchDTO.setName( searchProgram.getName() );
			searchProgramsPerSearchDTO.setDisplayName( searchProgram.getDisplayName() );
			searchProgramsPerSearchDTO.setVersion( searchProgram.getVersion() );
			searchProgramsPerSearchDTO.setDescription( searchProgram.getDescription() );
			
			
			SearchProgramsPerSearchDAO.getInstance().save( searchProgramsPerSearchDTO );
			
			SearchProgramEntry searchProgramEntry = new SearchProgramEntry();
			
			searchProgramEntry.setSearchProgramsPerSearchDTO( searchProgramsPerSearchDTO );
			
			searchProgramEntryMap.put( searchProgramsPerSearchDTO.getName(), searchProgramEntry );
			
			
			
			processReportedPeptideAnnotationTypes( searchProgram, searchProgramEntry, searchProgramsPerSearchDTO.getId(), searchId, searchProgramInfo );

			processPsmAnnotationTypes( searchProgram, searchProgramEntry, searchProgramsPerSearchDTO.getId(), searchId, searchProgramInfo );
		}
		
		
		
		return searchProgramEntryMap;
	}
	
	
	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @throws Exception 
	 */
	private void processReportedPeptideAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {
		
		processFilterableReportedPeptideAnnotationTypes( searchProgram, searchProgramEntry, searchProgramId, searchId, searchProgramInfo );
		
		processDescriptiveReportedPeptideAnnotationTypes( searchProgram, searchProgramEntry, searchProgramId, searchId, searchProgramInfo );
	}
	

	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @throws Exception 
	 */
	private void processFilterableReportedPeptideAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {

		
		Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap = 
				searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap();
		
		if ( reportedPeptideAnnotationTypeDTOMap == null ) {

			reportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			searchProgramEntry.setReportedPeptideAnnotationTypeDTOMap( reportedPeptideAnnotationTypeDTOMap );
		}
		


		
		String searchProgramName = searchProgram.getName();
		
		List<SearchAnnotation> reportedPeptideAnnotationSortOrderSearchAnnotationList = 
				searchProgramInfo.getAnnotationSortOrder().getReportedPeptideAnnotationSortOrder().getSearchAnnotation();
		

		List<SearchAnnotation> visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList = 
				searchProgramInfo.getDefaultVisibleAnnotations().getVisibleReportedPeptideAnnotations().getSearchAnnotation();
		
		
		SearchProgram.ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes =
				searchProgram.getReportedPeptideAnnotationTypes();

		if ( reportedPeptideAnnotationTypes == null ) {
			
			String msg = "No Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		FilterablePeptideAnnotationTypes filterablePeptideAnnotationTypes =
				reportedPeptideAnnotationTypes.getFilterablePeptideAnnotationTypes();

		if ( filterablePeptideAnnotationTypes == null ) {
			
			String msg = "No Reported Peptide Filterable Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		List<FilterablePeptideAnnotationType> filterablePeptideAnnotationTypeList =
				filterablePeptideAnnotationTypes.getFilterablePeptideAnnotationType();
		

		if ( filterablePeptideAnnotationTypeList == null ) {
			
			String msg = "No Reported Peptide Filterable Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		AnnotationTypeDAO annotationTypeDAO =
				AnnotationTypeDAO.getInstance();
		
		for ( FilterablePeptideAnnotationType filterablePeptideAnnotationType : filterablePeptideAnnotationTypeList ) {
			
			String annotationTypeName = filterablePeptideAnnotationType.getName();
			
			Integer annotationTypeSortOrder = getAnnotationTypeSortOrder( annotationTypeName, searchProgramName, reportedPeptideAnnotationSortOrderSearchAnnotationList );
			
			boolean annotationTypeDefaultVisible = getAnnotationTypeDefaultVisible( annotationTypeName, searchProgramName, visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList );
			
			Integer annotationTypeDisplayOrder = getAnnotationTypeDisplayOrder( annotationTypeName, searchProgramName, visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList );
			
			
			
			AnnotationTypeDTO item = new AnnotationTypeDTO();
			
			item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
			item.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PEPTIDE );

			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = new AnnotationTypeFilterableDTO();
			
			item.setAnnotationTypeFilterableDTO( annotationTypeFilterableDTO );

			item.setSearchId( searchId );
			
			item.setSearchProgramsPerSearchId( searchProgramId );
			
			item.setName( annotationTypeName );

			String filterDirectionString = filterablePeptideAnnotationType.getFilterDirection().value();
			
			FilterDirectionType filterDirectionType = FilterDirectionType.fromValue(filterDirectionString);
			annotationTypeFilterableDTO.setFilterDirectionType( filterDirectionType );
			
			annotationTypeFilterableDTO.setDefaultFilter( filterablePeptideAnnotationType.isDefaultFilter() );
			
			BigDecimal defaultFilterValue = filterablePeptideAnnotationType.getDefaultFilterValue();
			
			if ( defaultFilterValue != null ) {

				annotationTypeFilterableDTO.setDefaultFilterValue( defaultFilterValue.doubleValue() );
				annotationTypeFilterableDTO.setDefaultFilterValueString( defaultFilterValue.toString() );
			}
			
			annotationTypeFilterableDTO.setSortOrder( annotationTypeSortOrder );
			item.setDefaultVisible( annotationTypeDefaultVisible );
			item.setDisplayOrder( annotationTypeDisplayOrder );
			
			item.setDescription( filterablePeptideAnnotationType.getDescription() );

			annotationTypeDAO.saveToDatabase( item );
			
			reportedPeptideAnnotationTypeDTOMap.put( item.getName(), item );
		}
		
	}
	
	
	

	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @param filterDirectionStringIdMap
	 * @throws Exception 
	 */
	private void processDescriptiveReportedPeptideAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {


		Map<String, AnnotationTypeDTO> reportedPeptideAnnotationTypeDTOMap = 
				searchProgramEntry.getReportedPeptideAnnotationTypeDTOMap();
		
		if ( reportedPeptideAnnotationTypeDTOMap == null ) {

			reportedPeptideAnnotationTypeDTOMap = new HashMap<>();
			searchProgramEntry.setReportedPeptideAnnotationTypeDTOMap( reportedPeptideAnnotationTypeDTOMap );
		}
		
		String searchProgramName = searchProgram.getName();
		
		

		List<SearchAnnotation> visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList = 
				searchProgramInfo.getDefaultVisibleAnnotations().getVisibleReportedPeptideAnnotations().getSearchAnnotation();
		
		
		SearchProgram.ReportedPeptideAnnotationTypes reportedPeptideAnnotationTypes =
				searchProgram.getReportedPeptideAnnotationTypes();

		if ( reportedPeptideAnnotationTypes == null ) {
			
			String msg = "No Reported Peptide Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		DescriptivePeptideAnnotationTypes descriptivePeptideAnnotationTypes =
				reportedPeptideAnnotationTypes.getDescriptivePeptideAnnotationTypes();

		if ( descriptivePeptideAnnotationTypes == null ) {
			
			String msg = "No Reported Peptide Descriptive Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		List<DescriptivePeptideAnnotationType> descriptivePeptideAnnotationTypeList =
				descriptivePeptideAnnotationTypes.getDescriptivePeptideAnnotationType();
		
		if ( descriptivePeptideAnnotationTypeList == null ) {
			
			String msg = "No Reported Peptide Descriptive Annotation Types for search program name: " + searchProgram.getName();
			log.warn(msg);
			return;
		}
		
		AnnotationTypeDAO srchPgmDescriptiveReportedPeptideAnnotationTypeDAO =
				AnnotationTypeDAO.getInstance();
		
		for ( DescriptivePeptideAnnotationType descriptivePeptideAnnotationType : descriptivePeptideAnnotationTypeList ) {
			
			String annotationTypeName = descriptivePeptideAnnotationType.getName();
			
			boolean annotationTypeDefaultVisible = getAnnotationTypeDefaultVisible( annotationTypeName, searchProgramName, visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList );

			Integer annotationTypeDisplayOrder = getAnnotationTypeDisplayOrder( annotationTypeName, searchProgramName, visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList );
			
			AnnotationTypeDTO item = new AnnotationTypeDTO();
			
			item.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PEPTIDE );
			item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );

			item.setSearchId( searchId );
			
			item.setSearchProgramsPerSearchId( searchProgramId );
			
			item.setName( descriptivePeptideAnnotationType.getName() );
			
			item.setDefaultVisible(annotationTypeDefaultVisible);
			item.setDisplayOrder( annotationTypeDisplayOrder );

			item.setDescription( descriptivePeptideAnnotationType.getDescription() );

			srchPgmDescriptiveReportedPeptideAnnotationTypeDAO.saveToDatabase( item );
			
			reportedPeptideAnnotationTypeDTOMap.put( item.getName(), item );
		}
		
	}
	

	
	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @throws Exception 
	 */
	private void processPsmAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {
		
		processFilterablePsmAnnotationTypes( searchProgram, searchProgramEntry, searchProgramId, searchId, searchProgramInfo );
		
		processDescriptivePsmAnnotationTypes( searchProgram, searchProgramEntry, searchProgramId, searchId, searchProgramInfo );
	}

	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @param searchProgramInfo
	 * @throws Exception 
	 */
	private void processFilterablePsmAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {


		Map<String, AnnotationTypeDTO> psmAnnotationTypeDTOMap = 
				searchProgramEntry.getPsmAnnotationTypeDTOMap();
		
		if ( psmAnnotationTypeDTOMap == null ) {

			psmAnnotationTypeDTOMap = new HashMap<>();
			searchProgramEntry.setPsmAnnotationTypeDTOMap( psmAnnotationTypeDTOMap );
		}
		
		String searchProgramName = searchProgram.getName();
		
		List<SearchAnnotation> psmAnnotationSortOrderSearchAnnotationList = 
				searchProgramInfo.getAnnotationSortOrder().getPsmAnnotationSortOrder().getSearchAnnotation();
		

		List<SearchAnnotation> visiblePsmAnnotationsSearchAnnotationList = 
				searchProgramInfo.getDefaultVisibleAnnotations().getVisiblePsmAnnotations().getSearchAnnotation();
		
		

		
		
		SearchProgram.PsmAnnotationTypes psmAnnotationTypes =
				searchProgram.getPsmAnnotationTypes();

		FilterablePsmAnnotationTypes filterablePsmAnnotationTypes =
				psmAnnotationTypes.getFilterablePsmAnnotationTypes();
		
		List<FilterablePsmAnnotationType> filterablePsmAnnotationTypeList =
				filterablePsmAnnotationTypes.getFilterablePsmAnnotationType();
		
		AnnotationTypeDAO srchPgmFilterablePsmAnnotationTypeDAO =
				AnnotationTypeDAO.getInstance();
		
		for ( FilterablePsmAnnotationType filterablePsmAnnotationType : filterablePsmAnnotationTypeList ) {

			String annotationTypeName = filterablePsmAnnotationType.getName();
			
			Integer annotationTypeSortOrder = getAnnotationTypeSortOrder( annotationTypeName, searchProgramName, psmAnnotationSortOrderSearchAnnotationList );
			
			boolean annotationTypeDefaultVisible = getAnnotationTypeDefaultVisible( annotationTypeName, searchProgramName, visiblePsmAnnotationsSearchAnnotationList );

			Integer annotationTypeDisplayOrder = getAnnotationTypeDisplayOrder( annotationTypeName, searchProgramName, visiblePsmAnnotationsSearchAnnotationList );

			
			
			AnnotationTypeDTO item = new AnnotationTypeDTO();

			AnnotationTypeFilterableDTO annotationTypeFilterableDTO = new AnnotationTypeFilterableDTO();
			
			item.setAnnotationTypeFilterableDTO( annotationTypeFilterableDTO );
			
			item.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PSM );
			item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.FILTERABLE );
			
			item.setSearchId( searchId );
			
			item.setSearchProgramsPerSearchId( searchProgramId );

			item.setName( filterablePsmAnnotationType.getName() );


			String filterDirectionString = filterablePsmAnnotationType.getFilterDirection().value();
			
			FilterDirectionType filterDirectionType = FilterDirectionType.fromValue( filterDirectionString );
			annotationTypeFilterableDTO.setFilterDirectionType( filterDirectionType );
			
			annotationTypeFilterableDTO.setDefaultFilter( filterablePsmAnnotationType.isDefaultFilter() );


			BigDecimal defaultFilterValue = filterablePsmAnnotationType.getDefaultFilterValue();
			
			if ( defaultFilterValue != null ) {

				annotationTypeFilterableDTO.setDefaultFilterValue( defaultFilterValue.doubleValue() );
				annotationTypeFilterableDTO.setDefaultFilterValueString( defaultFilterValue.toString() );
			}

			annotationTypeFilterableDTO.setSortOrder( annotationTypeSortOrder );
			item.setDefaultVisible( annotationTypeDefaultVisible );
			item.setDisplayOrder( annotationTypeDisplayOrder );

			item.setDescription( filterablePsmAnnotationType.getDescription() );

			srchPgmFilterablePsmAnnotationTypeDAO.saveToDatabase( item );
			
			psmAnnotationTypeDTOMap.put( item.getName(), item );
		}
		
	}
	

	/**
	 * @param searchProgram
	 * @param searchProgramEntry
	 * @param searchProgramId
	 * @param filterDirectionStringIdMap
	 * @throws Exception 
	 */
	private void processDescriptivePsmAnnotationTypes( SearchProgram searchProgram, SearchProgramEntry searchProgramEntry, int searchProgramId, int searchId, SearchProgramInfo searchProgramInfo ) throws Exception {

		Map<String, AnnotationTypeDTO> psmAnnotationTypeDTOMap = 
				searchProgramEntry.getPsmAnnotationTypeDTOMap();
		
		if ( psmAnnotationTypeDTOMap == null ) {

			psmAnnotationTypeDTOMap = new HashMap<>();
			searchProgramEntry.setPsmAnnotationTypeDTOMap( psmAnnotationTypeDTOMap );
		}
		
		
		String searchProgramName = searchProgram.getName();

		List<SearchAnnotation> visiblePsmAnnotationsSearchAnnotationList = 
				searchProgramInfo.getDefaultVisibleAnnotations().getVisiblePsmAnnotations().getSearchAnnotation();
		
		

		SearchProgram.PsmAnnotationTypes psmAnnotationTypes =
				searchProgram.getPsmAnnotationTypes();

		DescriptivePsmAnnotationTypes descriptivePsmAnnotationTypes =
				psmAnnotationTypes.getDescriptivePsmAnnotationTypes();
		
		List<DescriptivePsmAnnotationType> descriptivePsmAnnotationTypeList =
				descriptivePsmAnnotationTypes.getDescriptivePsmAnnotationType();
		
		AnnotationTypeDAO srchPgmDescriptivePsmAnnotationTypeDAO =
				AnnotationTypeDAO.getInstance();
		
		for ( DescriptivePsmAnnotationType descriptivePsmAnnotationType : descriptivePsmAnnotationTypeList ) {
			
			String annotationTypeName = descriptivePsmAnnotationType.getName();
			
			boolean annotationTypeDefaultVisible = getAnnotationTypeDefaultVisible( annotationTypeName, searchProgramName, visiblePsmAnnotationsSearchAnnotationList );

			Integer annotationTypeDisplayOrder = getAnnotationTypeDisplayOrder( annotationTypeName, searchProgramName, visiblePsmAnnotationsSearchAnnotationList );
			
			
			AnnotationTypeDTO item = new AnnotationTypeDTO();
			
			item.setPsmPeptideAnnotationType( PsmPeptideAnnotationType.PSM );
			item.setFilterableDescriptiveAnnotationType( FilterableDescriptiveAnnotationType.DESCRIPTIVE );

			item.setSearchId( searchId );
			
			item.setSearchProgramsPerSearchId( searchProgramId );
			
			item.setName( descriptivePsmAnnotationType.getName() );
			item.setDefaultVisible( annotationTypeDefaultVisible );
			item.setDisplayOrder( annotationTypeDisplayOrder );

			item.setDescription( descriptivePsmAnnotationType.getDescription() );

			srchPgmDescriptivePsmAnnotationTypeDAO.saveToDatabase( item );
			
			psmAnnotationTypeDTOMap.put( item.getName(), item );
		}
		
	}
	
	
	

	
	/**
	 * Utility Lookup.  Get position in AnnotationTypeSortOrder or return null if not found
	 * 
	 * 
	 * @param annotationTypeName
	 * @param searchProgramName
	 * @param annotationSortOrderSearchAnnotationList
	 * @return null if not found, otherwise the position in the list
	 */
	private Integer getAnnotationTypeSortOrder( String annotationTypeName, String searchProgramName, List<SearchAnnotation> annotationSortOrderSearchAnnotationList ) {
		
		int orderPositionCounter = 0;
		
		for ( SearchAnnotation searchAnnotationSortOrder : annotationSortOrderSearchAnnotationList ) {
			
			orderPositionCounter++;
			
			if ( annotationTypeName.equals( searchAnnotationSortOrder.getAnnotationName() )
					&& searchProgramName.equals( searchAnnotationSortOrder.getSearchProgram() ) ) {
				
				return orderPositionCounter;  // EARLY EXIT
			}
		}
		
		return null;  //  for no match found
	}

	
	/**
	 * Utility Lookup.  Return true if in AnnotationTypeDefaultVisible or return false if not found
	 * 
	 * @param annotationTypeName
	 * @param searchProgramName
	 * @param visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList
	 * @return Return true if in AnnotationTypeDefaultVisible or return false if not found
	 */
	private boolean getAnnotationTypeDefaultVisible( String annotationTypeName, String searchProgramName, List<SearchAnnotation> visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList ) {

		for ( SearchAnnotation searchAnnotationDefaultVisible : visibleReportedPeptideDefaultVisibleAnnotationsSearchAnnotationList ) {
			
			if ( annotationTypeName.equals( searchAnnotationDefaultVisible.getAnnotationName() )
					&& searchProgramName.equals( searchAnnotationDefaultVisible.getSearchProgram() ) ) {
				
				return true;  // EARLY EXIT
			}
		}
		
		return false;  //  for no match found
	}
	

	/**
	 * Utility Lookup.  Get position in AnnotationTypeDefaultVisible or return null if not found
	 * 
	 * 
	 * @param annotationTypeName
	 * @param searchProgramName
	 * @param annotationSortOrderSearchAnnotationList
	 * @return null if not found, otherwise the position in the list
	 */
	private Integer getAnnotationTypeDisplayOrder( String annotationTypeName, String searchProgramName, List<SearchAnnotation> annotationDefaultVisibleSearchAnnotationList ) {
		
		int orderPositionCounter = 0;
		
		for ( SearchAnnotation searchAnnotationDefaultVisible : annotationDefaultVisibleSearchAnnotationList ) {
			
			orderPositionCounter++;
			
			if ( annotationTypeName.equals( searchAnnotationDefaultVisible.getAnnotationName() )
					&& searchProgramName.equals( searchAnnotationDefaultVisible.getSearchProgram() ) ) {
				
				return orderPositionCounter;  // EARLY EXIT
			}
		}
		
		return null;  //  for no match found
	}

	
	
	
	
}
